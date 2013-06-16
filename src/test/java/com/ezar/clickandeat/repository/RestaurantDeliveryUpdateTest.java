package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Restaurant;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class RestaurantDeliveryUpdateTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Test
    @Ignore
    public void testUpdateDeliveryRadius() throws Exception {
        List<Restaurant> scrapedRestaurants = mongoOperations.find(new Query(where("externalId").ne(null)),Restaurant.class);
        for(Restaurant restaurant:scrapedRestaurants) {
            restaurant.getDeliveryOptions().setDeliveryRadiusInKilometres(2d);
            mongoOperations.save(restaurant);
        }
    }
}
