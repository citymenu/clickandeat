package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.cache.ClusteredCache;
import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.util.FilterUtils;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.util.SequenceGenerator;
import com.ezar.clickandeat.web.controller.helper.Filter;
import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.Circle;
import org.springframework.data.mongodb.core.geo.Metrics;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.repository.query.QueryUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom, InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(RestaurantRepositoryImpl.class);

    private static final double DIVISOR = Metrics.KILOMETERS.getMultiplier();

    private static final int MAX_RECOMMENDATIONS = 9;
    
    private static final int REFRESH_TIMEOUT = 1000 * 60 * 15; // Refresh recommendations list every 15 minutes
    
    
    @Autowired
    private MongoOperations operations;

    @Autowired
    private GeoLocationService locationService;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private ClusteredCache clusteredCache;

    private double maxDistance;

    private long lastRefreshed = 0l;    

    private boolean usecache = true;

    private List<Restaurant> recommendations;
    

    @Override
    public void afterPropertiesSet() throws Exception {
        operations.indexOps(Restaurant.class).ensureIndex(new GeospatialIndex("address.location"));
    }
     

    @Override
    public Restaurant create() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(sequenceGenerator.getNextSequence());
        return restaurant;
    }


    @Override
    public List<Restaurant> getPage(Pageable pageable) {
        Query query = new Query(where("deleted").ne(true)).with(pageable);
        return operations.find(query,Restaurant.class);
    }

    
    @Override
    public long countActive() {
        Query query = new Query(where("deleted").ne(true));
        return operations.count(query,Restaurant.class);
    }

    @Override
    public List<Restaurant> getRecommended() {
        long now = System.currentTimeMillis();
        if( recommendations == null || now > lastRefreshed + REFRESH_TIMEOUT ) {
            Query query = new Query(where("listOnSite").is(true).and("recommended").is(true).and("deleted").ne(true)
                    .norOperator(where("testMode").is(true).and("phoneOrdersOnly").ne(true)));
            List<Restaurant> restaurants = operations.find(query,Restaurant.class);
            if( restaurants.size() <= MAX_RECOMMENDATIONS ) {
                if( restaurants.size() % 3 != 0 ) {
                    recommendations = restaurants.subList(0,restaurants.size() - restaurants.size() % 3 ); // Must be a multiple of 3
                }
                else {
                    recommendations = restaurants;
                }
            }
            else {
                List<Restaurant> candidates = new ArrayList<Restaurant>();
                // Add more instances of a restaurant depending on search ranking (0 - 100)
                for( Restaurant restaurant: restaurants ) {
                    int rankCount = restaurant.getSearchRanking() / 9 + 1; // 1 to 10
                    for( int j = 0; j <= rankCount; j++ ) {
                        candidates.add(restaurant);
                    }
                }
                List<Restaurant> randomList = new ArrayList<Restaurant>();
                Random random = new Random();
                while (randomList.size() < MAX_RECOMMENDATIONS ) {
                    int nextIndex = random.nextInt(candidates.size());
                    Restaurant candidate = candidates.get(nextIndex);
                    if( !randomList.contains(candidate)) {
                        randomList.add(candidate);
                    }
                }
                Collections.sort(randomList, new Comparator<Restaurant>() {
                    public int compare(Restaurant r1, Restaurant r2) {
                        int rankingDifference = r1.getSearchRanking() - r2.getSearchRanking();
                        if( rankingDifference == 0 ) {
                            return r1.getCreated().compareTo(r2.getCreated());
                        }
                        else if( rankingDifference < 0 ) {
                            return 1;
                        }
                        else {
                            return -1;
                        }
                    }
                });
                recommendations = randomList;
            }
            lastRefreshed = now;
        }
        return recommendations;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Restaurant findByRestaurantId(String restaurantId) {
        if( restaurantId == null ) {
            throw new IllegalArgumentException("restaurantId must not be null");
        }
        Restaurant restaurant = null;
        if(usecache) {
            restaurant = clusteredCache.get(Restaurant.class, restaurantId);
        }
        if( restaurant == null ) {
            restaurant = operations.findOne(query(where("restaurantId").is(restaurantId)),Restaurant.class);
            if( restaurant != null && usecache ) {
                clusteredCache.store(Restaurant.class,restaurantId,restaurant);
            }
        }
        return restaurant;
    }

    @Override
    public Restaurant findByName(String name) {
        return operations.findOne(query(where("name").is(name)),Restaurant.class);
    }

    @Override
    public Restaurant findByExternalId(String externalId) {
        return operations.findOne(query(where("externalId").is(externalId)),Restaurant.class);
    }

    @Override
    public List<Restaurant> pageByRestaurantName(Pageable pageable, String restaurantName, List<Filter> filters) {
        Query query = StringUtils.hasText(restaurantName)? new Query(where("name").regex(restaurantName,"i").and("deleted").ne(true)): new Query(where("deleted").ne(true));
        FilterUtils.applyFilters(query, filters);
        query.with(pageable);
        return operations.find(query, Restaurant.class);
    }


    @Override
    public long count(String restaurantName, List<Filter> filters) {
        Query query = StringUtils.hasText(restaurantName)? new Query(where("name").regex(restaurantName).and("deleted").ne(true)): new Query(where("deleted").ne(true));
        FilterUtils.applyFilters(query,filters);
        return operations.count(query, Restaurant.class);
    }


    @Override
    @SuppressWarnings("unchecked")
    public Restaurant saveRestaurant(Restaurant restaurant) {

        if( restaurant.getAddress() != null ) {
            GeoLocation location = locationService.getLocation(restaurant.getAddress());
            if( location != null ) {
                restaurant.getAddress().setLocation(location.getLocation());
            }
        }

        // Update category and menu item identifiers
        if( restaurant.getMenu() != null ) {
            for( MenuCategory menuCategory: restaurant.getMenu().getMenuCategories()) {
                if( !StringUtils.hasText(menuCategory.getCategoryId())) {
                    menuCategory.setCategoryId(sequenceGenerator.getNextSequence());
                }
                for(MenuItem menuItem: menuCategory.getMenuItems()) {
                    if(!StringUtils.hasText(menuItem.getItemId())) {
                        menuItem.setItemId(sequenceGenerator.getNextSequence());
                    }
                }
            }
        }

        // Update discount identifiers
        for( Discount discount: restaurant.getDiscounts()) {
            if( !StringUtils.hasText(discount.getDiscountId())) {
                discount.setDiscountId(sequenceGenerator.getNextSequence());
            }
        }

        // Update special offer item identifiers
        for( SpecialOffer specialOffer: restaurant.getSpecialOffers()) {
            if( !StringUtils.hasText(specialOffer.getSpecialOfferId())) {
                specialOffer.setSpecialOfferId(sequenceGenerator.getNextSequence());
            }
        }

        if(usecache) {
            clusteredCache.remove(Restaurant.class, restaurant.getRestaurantId());
        }
        
        // Update created and last update times
        long now = new DateTime().getMillis();
        if( restaurant.getCreated() == null ) {
            restaurant.setCreated(now);
        }
        restaurant.setLastUpdated(now);

        // Save the restaurant
        operations.save(restaurant);
        return restaurant;
    }


    @Override
    public List<Restaurant> quickLaunch() {
        Query query = new Query(where("deleted").ne(true));
        return operations.find(query, Restaurant.class);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void deleteRestaurant(Restaurant restaurant) {
        operations.remove(query(where("restaurantId").is(restaurant.getRestaurantId())), Restaurant.class);
        if(usecache) {
            clusteredCache.remove(Restaurant.class, restaurant.getRestaurantId());
        }
    }


    @Override
    public Pair<List<Restaurant>,SortedMap<String,Integer>> search(Search search) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking up restaurants serving matching search: " + search);
        }

        // Return values
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        SortedMap<String,Integer> cuisineCount = new TreeMap<String, Integer>();

        // Build the query including location if set
        GeoLocation geoLocation = search.getLocation();
        Query query;
        if( geoLocation == null ) {
            query = new Query();
        }
        else {
            Double radius = Math.max(maxDistance,geoLocation.getRadius()) / DIVISOR;
            Circle circle = new Circle(geoLocation.getLocation()[0], geoLocation.getLocation()[1], radius);
            query = new Query(where("address.location").withinSphere(circle));
        }
        query.addCriteria(where("listOnSite").is(true).and("deleted").ne(true)
                .norOperator(where("testMode").is(true).and("phoneOrdersOnly").ne(true)));

        // Add scope variables to the map/reduce query
        Map<String,Object> scopeVariables = new HashMap<String,Object>();
        scopeVariables.put("cuisine",search.getCuisine() == null? null: search.getCuisine());
        scopeVariables.put("address", search.getLocation() == null? null: search.getLocation().getAddress().toUpperCase());
        scopeVariables.put("lat1",search.getLocation() == null? null: search.getLocation().getLocation()[0]);
        scopeVariables.put("lon1",search.getLocation() == null? null: search.getLocation().getLocation()[1]);
        scopeVariables.put("radius", search.getLocation() == null? null: search.getLocation().getRadius());
        MapReduceOptions options = MapReduceOptions.options();
        options.scopeVariables(scopeVariables);
        options.outputTypeInline(); // Important!
        options.finalizeFunction("classpath:/mapreduce/finalize.js");

        MapReduceResults<ValueObject> results = operations.mapReduce(query, "restaurants", "classpath:/mapreduce/map.js", "classpath:/mapreduce/reduce.js", options, ValueObject.class);
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executed map reduce query in: " + results.getTiming().getTotalTime() + "ms");
        }

        // Build results
        DateTime now = new DateTime();
        for (ValueObject valueObject : results) {
            Map<String,Object> values = valueObject.getValue();
            if( values.get("restaurant") != null ) {
                Restaurant restaurant = (Restaurant)values.get("restaurant");
                restaurant.setDistanceToSearchLocation((Double)values.get("distance"));
                restaurant.setOpen(restaurant.isOpen(now));
                restaurants.add((Restaurant)values.get("restaurant"));
            }
            else {
                int count = ((Double)values.get("count")).intValue();
                cuisineCount.put(valueObject.getId(), count);
            }
        }

        // Return the results
        return new Pair<List<Restaurant>, SortedMap<String, Integer>>(restaurants, cuisineCount);

    }


    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String,List<String>> getCuisinesByLocation() {
        
        if( countActive() == 0 ) {
            return new HashMap<String, List<String>>();
        }
        
        Query query = new Query(where("listOnSite").is(true).and("deleted").ne(true).and("testMode").ne(true));

        // Add scope variables to the map/reduce query
        MapReduceOptions options = MapReduceOptions.options();
        options.outputTypeInline(); // Important!

        MapReduceResults<ValueObject> results = operations.mapReduce(query, "restaurants", "classpath:/mapreduce/footermap.js", "classpath:/mapreduce/footerreduce.js", options, ValueObject.class);
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executed map reduce query in: " + results.getTiming().getTotalTime() + "ms");
        }

        // Build results
        Map<String,List<String>> cuisinesByLocation = new HashMap<String, List<String>>();
        for (ValueObject valueObject : results) {
            String location = valueObject.getId();
            List<String> cuisines = (List<String>)valueObject.getValue().get("cuisines");
            cuisinesByLocation.put(location, cuisines);
        }
        return cuisinesByLocation;
    }
    

    @Required
    @Value(value="${location.maxDistance}")
    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }


    @Override
    public void addRestaurantUpdate(String restaurantId, String text) {
        RestaurantUpdate restaurantUpdate = new RestaurantUpdate();
        restaurantUpdate.setText(text);
        restaurantUpdate.setUpdateTime(new DateTime());
        Update update = new BasicUpdate(new BasicDBObject()).push("restaurantUpdates",restaurantUpdate);
        operations.updateFirst(query(where("restaurantId").is(restaurantId)),update,Restaurant.class);
    }

    public void setUsecache(boolean usecache) {
        this.usecache = usecache;
    }
}
