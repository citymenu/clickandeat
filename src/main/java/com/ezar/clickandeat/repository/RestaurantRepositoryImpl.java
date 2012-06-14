package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.util.SequenceGenerator;
import org.apache.log4j.Logger;
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
    
    private double maxDistance;

    @Override
    public void afterPropertiesSet() throws Exception {
        operations.ensureIndex(new GeospatialIndex("address.location"),Restaurant.class);
    }

    @Override
    public Restaurant create() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(sequenceGenerator.getNextSequence());
        restaurant.setOpeningTimes(new OpeningTimes());
        restaurant.setDeliveryOptions(new DeliveryOptions());
        restaurant.setMainContact(new Person());
        restaurant.setNotificationOptions(new NotificationOptions());
        restaurant.setMenu(new Menu());
        return restaurant;
    }

    @Override
    public Restaurant findByRestaurantId(String restaurantId) {
        return operations.findOne(query(where("restaurantId").is(restaurantId)),Restaurant.class);
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        if( restaurant.getAddress() != null && StringUtils.hasText(restaurant.getAddress().getPostCode())) {
            double[] location = locationService.getLocation(restaurant.getAddress().getPostCode());
            restaurant.getAddress().setLocation(location);
        }
        operations.save(restaurant);
        return restaurant;
    }


    public RestaurantRepositoryImpl() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public List<Restaurant> search(String location, String cuisine, String sort, String dir ) {

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

        // Specify cuisine if required
        if( StringUtils.hasText(cuisine)) {
            query.addCriteria(where("cuisines").all(cuisine));
        }
        
        // Specify sort order if specified
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

        // Iterate over the results to determine which restaurants will serve the location
        List<Restaurant> availableRestaurants = new ArrayList<Restaurant>();
        for( Restaurant restaurant: restaurants ) {
            double[] restaurantLocation = restaurant.getAddress().getLocation();
            DeliveryOptions deliveryOptions = restaurant.getDeliveryOptions();
            if( deliveryOptions.getDeliveryRadiusInKilometres() != null ) {
                double distance = locationService.getDistance(geoLocation,restaurantLocation);
                if( distance <= deliveryOptions.getDeliveryRadiusInKilometres()) {
                    availableRestaurants.add(restaurant);
                    break;
                }
                for( String deliveryLocation: deliveryOptions.getAreasDeliveredTo()) {
                    if(deliveryLocation.toUpperCase().replace(" ", "").startsWith(lookupLocation)) {
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

}
