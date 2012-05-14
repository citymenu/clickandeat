package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import org.apache.log4j.Logger;
import org.joda.time.chrono.AssembledChronology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.Distance;
import org.springframework.data.mongodb.core.geo.Metric;
import org.springframework.data.mongodb.core.geo.Metrics;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(RestaurantRepositoryImpl.class);
    
    private static final double DEFAULT_MAX_DISTANCE = 10d;

    private static final double DIVISOR = Metrics.KILOMETERS.getMultiplier();

    @Autowired
    private MongoOperations operations;

    private double maxDistance;


    @Override
    public List<Restaurant> findRestaurantsServingLocation(double[] location) {
        Point point = new Point(location[0],location[1]);
        Query query = new Query(Criteria.where("address.location").nearSphere(point).maxDistance( maxDistance / DIVISOR ));
        query.fields().exclude("menu");
        List<Restaurant> restaurants = operations.find(query,Restaurant.class);

        List<Restaurant> availableRestaurants = new ArrayList<Restaurant>();
        for( Restaurant restaurant: restaurants ) {
            
        }
        
        return availableRestaurants;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }
}
