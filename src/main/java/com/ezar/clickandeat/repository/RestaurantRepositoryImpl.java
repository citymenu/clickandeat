package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.SequenceGenerator;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.mail.search.ReceivedDateTerm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom, InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(RestaurantRepositoryImpl.class);
    
    private static final double DIVISOR = Metrics.KILOMETERS.getMultiplier();

    private static final String TOPIC = "restaurant";
    
    @Autowired
    private MongoOperations operations;
    
    @Autowired
    private LocationService locationService;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private RedisTemplate redisTemplate;
    
    private ConcurrentMap<String,Restaurant> restaurantCache = new ConcurrentHashMap<String,Restaurant>();
    
    private double maxDistance;

    private String timeZone;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        operations.ensureIndex(new GeospatialIndex("address.location"), Restaurant.class);
        redisTemplate.setExposeConnection(true);
        RestaurantUpdateSubscriber subscriber = new RestaurantUpdateSubscriber(redisTemplate,TOPIC);
        Thread subscriberThread = new Thread(subscriber);
        subscriberThread.setDaemon(true);
        subscriberThread.start();
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
        restaurant = restaurantCache.get(restaurantId);
        if( restaurant == null ) {
            restaurant = operations.findOne(query(where("restaurantId").is(restaurantId)),Restaurant.class);
            if( restaurant != null ) {
                restaurantCache.put(restaurantId,restaurant);
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
        
        operations.save(restaurant);
        restaurantCache.put(restaurant.getRestaurantId(),restaurant);

        // Publish update message to restaurant topic
        Map<String,String> map = new HashMap<String, String>();
        map.put("action","update");
        map.put("restaurant",JSONUtils.serialize(restaurant));
        redisTemplate.getConnectionFactory().getConnection().publish("restaurant".getBytes(), JSONUtils.serialize(map).getBytes());

        return restaurant;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void deleteRestaurant(Restaurant restaurant) {
        operations.remove(query(where("restaurantId").is(restaurant.getRestaurantId())), Restaurant.class);

        // Publish delete message to restaurant topic
        Map<String,String> map = new HashMap<String, String>();
        map.put("action","delete");
        map.put("restaurantId", restaurant.getRestaurantId());
        redisTemplate.getConnectionFactory().getConnection().publish("restaurant".getBytes(), JSONUtils.serialize(map).getBytes());
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
        LocalDate today = new LocalDate(DateTimeZone.forID(timeZone));
        LocalTime now = new LocalTime(DateTimeZone.forID(timeZone));
        
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
                    restaurant.setOpenForDelivery(restaurant.isOpenForDelivery(today,now));
                    availableRestaurants.add(restaurant);
                    continue;
                }
                for( String deliveryLocation: deliveryOptions.getAreasDeliveredTo()) {
                    if(deliveryLocation.toUpperCase().replace(" ", "").startsWith(lookupLocation)) {
                        // Set transient open for delivery property for search result ordering
                        restaurant.setOpenForDelivery(restaurant.isOpenForDelivery(today,now));
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
     * Listens to restaurant updates via Redis
     */
    
    private final class RestaurantUpdateSubscriber implements Runnable, MessageListener {
        
        private final RedisConnection connection;
        
        private final byte[] channel;

        /**
         * @param template
         * @param channel
         */
        
        private RestaurantUpdateSubscriber(RedisTemplate template, String channel) {
            this.connection = template.getConnectionFactory().getConnection();
            this.channel = channel.getBytes();
        }

        @Override
        public void run() {
            connection.subscribe(this,channel);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onMessage(Message message, byte[] pattern) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("Received message: " + message);
            }
            String content = new String(message.getBody());
            Map map = (Map)JSONUtils.deserialize(content);
            String action = (String)map.get("action");
            if("delete".equals(action)) {
                String restaurantId = (String)map.get("restaurantId");
                restaurantCache.remove(restaurantId);
            }
            else {
                String json = (String)map.get("restaurant");
                Restaurant restaurant = JSONUtils.deserialize(Restaurant.class,json);
                restaurantCache.put(restaurant.getRestaurantId(),restaurant);
            }
        }
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
