package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.Metrics;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Criteria;
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

    private String region;

    private double maxDistance;

    @Override
    public void afterPropertiesSet() throws Exception {
        operations.ensureIndex(new GeospatialIndex("address.location"),Restaurant.class);
    }

    @Override
    public Restaurant findByRestaurantId(String restaurantId) {
        return operations.findOne(query(where("restaurantId").is(restaurantId)),Restaurant.class);
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        if( restaurant.getAddress() != null && StringUtils.hasText(restaurant.getAddress().getPostCode())) {
            double[] location = locationService.getLocation(restaurant.getAddress().getPostCode(),region);
            restaurant.getAddress().setLocation(location);
        }
        operations.save(restaurant);
        return restaurant;
    }


    @Override
    public List<Restaurant> findRestaurantsServingPostCode(String postCode) {

        String lookupPostCode = postCode.toUpperCase().replace(" ","");
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking up restaurants serving postCode: " + postCode);
        }
        
        double[] location = locationService.getLocation(lookupPostCode,region);

        Query query = new Query(Criteria.where("address.location").nearSphere(new Point(location[0], location[1]))
                .maxDistance(maxDistance / DIVISOR));
        query.fields().exclude("menu");
        
        List<Restaurant> restaurants = operations.find(query,Restaurant.class);

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returned " + restaurants.size() + " restaurants, now checking delivery options");
        }

        List<Restaurant> availableRestaurants = new ArrayList<Restaurant>();
        for( Restaurant restaurant: restaurants ) {
            double[] restaurantLocation = restaurant.getAddress().getLocation();
            for( DeliveryOption deliveryOption: restaurant.getDeliveryOptions().getDeliveryOptions()) {
                if( deliveryOption.getDeliveryRadius() != null ) {
                    double distance = getDistance(location,restaurantLocation);
                    if( distance <= deliveryOption.getDeliveryRadius()) {
                        availableRestaurants.add(restaurant);
                        break;
                    }
                }
                for( String deliveryLocation: deliveryOption.getAreasDeliveredTo()) {
                    if(deliveryLocation.toUpperCase().replace(" ", "").startsWith(lookupPostCode)) {
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


    /**
     * Returns the distance in kilometres between two locations 
     * @param location1
     * @param location2
     * @return
     */
    
    private double getDistance(double[] location1, double[] location2) {

        double dLat = Math.toRadians(location1[0]-location2[0]);
        double dLon = Math.toRadians(location1[1]-location2[1]);

        double lat1 = Math.toRadians(location1[0]);
        double lat2 = Math.toRadians(location2[0]);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = DIVISOR * c;
    
        return d;
    }


    @Required
    @Value(value="${location.maxDistance}")
    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Required
    @Value(value="${location.region}")
    public void setRegion(String region) {
        this.region = region;
    }
}
