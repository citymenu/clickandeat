package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.cache.ClusteredCache;
import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.util.SequenceGenerator;
import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.Metrics;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.QueryUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom, InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(RestaurantRepositoryImpl.class);

    private static final double DIVISOR = Metrics.KILOMETERS.getMultiplier();

    private static final int MAX_RECOMMENDATIONS = 12;
    
    private static final int REFRESH_TIMEOUT = 1000 * 60 * 60; // Refresh recommendations list every hour
    
    
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
    
    private List<Restaurant> recommendations;
    

    @Override
    public void afterPropertiesSet() throws Exception {
        operations.ensureIndex(new GeospatialIndex("address.location"), Restaurant.class);
    }
     

    @Override
    public Restaurant create() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(sequenceGenerator.getNextSequence());
        return restaurant;
    }


    @Override
    public List<Restaurant> getPage(Pageable pageable) {
        Query query = new Query(where("deleted").ne(true));
        QueryUtils.applyPagination(query, pageable);
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
            Query query = new Query(where("listOnSite").is(true).and("recommended").is(true).and("deleted").ne(true));
            List<Restaurant> restaurants = recommendations = operations.find(query,Restaurant.class);
            if( restaurants.size() <= MAX_RECOMMENDATIONS ) {
                recommendations = restaurants;
            }
            else {
                List<Restaurant> randomList = new ArrayList<Restaurant>();
                Random random = new Random();
                for( int i = 0; i < MAX_RECOMMENDATIONS; i++ ) {
                    int nextIndex = random.nextInt(restaurants.size());
                    randomList.add(restaurants.get(nextIndex));
                }
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
        Restaurant restaurant;
        restaurant = clusteredCache.get(Restaurant.class, restaurantId);
        if( restaurant == null ) {
            restaurant = operations.findOne(query(where("restaurantId").is(restaurantId)),Restaurant.class);
            if( restaurant != null ) {
                clusteredCache.store(Restaurant.class,restaurantId,restaurant);
            }
        }
        return restaurant;
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

        clusteredCache.remove(Restaurant.class, restaurant.getRestaurantId());
        
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
    @SuppressWarnings("unchecked")
    public void deleteRestaurant(Restaurant restaurant) {
        operations.remove(query(where("restaurantId").is(restaurant.getRestaurantId())), Restaurant.class);
        clusteredCache.remove(Restaurant.class, restaurant.getRestaurantId());
    }


    @Override
    public Pair<List<Restaurant>,Map<String,Integer>> search(Search search) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking up restaurants serving matching search: " + search);
        }

        // Return values
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        Map<String,Integer> cuisineCount = new HashMap<String, Integer>();

        // Build the query including location if set
        GeoLocation geoLocation = search.getLocation();
        Query query = geoLocation != null? new Query(where("address.location").nearSphere(
                new Point(geoLocation.getLocation()[0], geoLocation.getLocation()[1])).maxDistance(Math.max(maxDistance,geoLocation.getRadius()) / DIVISOR)):
                new Query();
        query.addCriteria(where("listOnSite").is(true).and("deleted").ne(true));

        // Add scope variables to the map/reduce query
        Map<String,Object> scopeVariables = new HashMap<String,Object>();
        scopeVariables.put("cuisine",search.getCuisine() == null? null: search.getCuisine());
        scopeVariables.put("address", search.getLocation() == null? null: search.getLocation().getAddress());
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
        return new Pair<List<Restaurant>, Map<String, Integer>>(restaurants, cuisineCount);

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

}
