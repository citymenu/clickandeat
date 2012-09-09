package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.cache.ClusteredCache;
import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.util.SequenceGenerator;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.Metrics;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom, InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(RestaurantRepositoryImpl.class);
    
    private static final double DIVISOR = Metrics.KILOMETERS.getMultiplier();

    @Autowired
    private MongoOperations operations;
    
    @Autowired
    private LocationService locationService;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private ClusteredCache clusteredCache;
    
    private double maxDistance;

    private String timeZone;
    
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
        if( restaurant.getAddress() != null && StringUtils.hasText(restaurant.getAddress().getPostCode())) {
            double[] location = locationService.getLocation(restaurant.getAddress().getPostCode());
            restaurant.getAddress().setLocation(location);
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
    public List<Restaurant> search(Search search) {

        String location = search.getLocation();
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking up restaurants serving location: " + location);
        }

        if( !StringUtils.hasText(location)) {
            LOGGER.warn("Empty location value passed to 'search' method");
            return new ArrayList<Restaurant>();
        }
        
        String lookupLocation = location.toUpperCase().replace(" ","");
        
        // Build geolocation query
        double[] geoLocation = locationService.getLocation(lookupLocation);
        if( geoLocation == null ) {
            LOGGER.warn("No geolocation found for location " + location);
            return new ArrayList<Restaurant>();
        }
        
        Query query = new Query(where("address.location").nearSphere(new Point(geoLocation[0], geoLocation[1]))
                .maxDistance(maxDistance / DIVISOR));

        // Specify cuisines if required
        List<String> cuisines = search.getCuisines();
        if( cuisines != null && cuisines.size() > 0 ) {
            query.addCriteria(where("cuisines").in(cuisines));
        }
        
        // Specify sort order if specified
        String sort = search.getSort();
        String dir = search.getDir();
        if( StringUtils.hasText(sort) && StringUtils.hasLength(dir)) {
            query.sort().on(sort, "asc".equals(dir)?Order.ASCENDING: Order.DESCENDING );
        }

        // Exclude menu from results
        query.fields().exclude("menu");
        
        // Execute the query
        List<Restaurant> restaurants = operations.find(query,Restaurant.class);
        if( restaurants.size() == 0 ) {
            return restaurants;
        }

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returned " + restaurants.size() + " restaurants, now checking delivery options");
        }

        // Get the current time and date to determine if restaurants are open
        DateTime now = new DateTime();
        
        // Iterate over the results to determine which restaurants will serve the location
        List<Restaurant> availableRestaurants = new ArrayList<Restaurant>();
        for( Restaurant restaurant: restaurants ) {
            double[] restaurantLocation = restaurant.getAddress().getLocation();
            DeliveryOptions deliveryOptions = restaurant.getDeliveryOptions();
            if( deliveryOptions.getDeliveryRadiusInKilometres() != null ) {
                double distance = locationService.getDistance(geoLocation,restaurantLocation);
                if( LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Distance from location " + location + " to restaurant " + restaurant.getName() + " is " + distance);
                }
                // Set transient distance to location property for search result ordering
                restaurant.setDistanceToSearchLocation(distance);

                if( distance <= deliveryOptions.getDeliveryRadiusInKilometres()) {
                    // Set transient open for delivery property for search result ordering
                    restaurant.setOpenForDelivery(restaurant.isOpenForDelivery(now));
                    availableRestaurants.add(restaurant);
                    continue;
                }
                for( String deliveryLocation: deliveryOptions.getAreasDeliveredTo()) {
                    if(deliveryLocation.toUpperCase().replace(" ", "").startsWith(lookupLocation)) {
                        // Set transient open for delivery property for search result ordering
                        restaurant.setOpenForDelivery(restaurant.isOpenForDelivery(now));
                        availableRestaurants.add(restaurant);
                        break;
                    }
                }
            }
        }

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returning list of " + availableRestaurants.size() + " available restaurants");
        }
        
        return availableRestaurants;
    }

    
    @Required
    @Value(value="${location.maxDistance}")
    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    
    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
