package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.*;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class RestaurantSearchTest {

    private static final Logger LOGGER = Logger.getLogger(RestaurantSearchTest.class);
    
    @Autowired
    private RestaurantRepository repository;
    
    private String restaurantId = "testrestaurant";
    
    @Before
    public void setup() throws Exception {

        removeRestaurant(restaurantId);
        
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(restaurantId);
        
        Person mainContact = new Person();
        mainContact.setFirstName("test");
        mainContact.setLastName("owner");
        restaurant.setMainContact(mainContact);
        
        Address address = new Address();
        address.setPostCode("E18 2LG");
        restaurant.setAddress(address);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        DeliveryOption deliveryOption = new DeliveryOption();
        deliveryOption.setDeliveryRadius(3d);
        deliveryOption.getAreasDeliveredTo().add("E17");
        deliveryOption.getAreasDeliveredTo().add("E6");
        deliveryOptions.getDeliveryOptions().add(deliveryOption);
        restaurant.setDeliveryOptions(deliveryOptions);

        repository.saveRestaurant(restaurant);
        
    }

    
    @After
    public void tearDown() throws Exception {
        removeRestaurant(restaurantId);        
    }
    
    
    private void removeRestaurant(String restaurantId) throws Exception {
        Restaurant restaurant = repository.findByRestaurantId(restaurantId);
        if( restaurant != null ) {
            repository.deleteRestaurant(restaurant);
        }
    }

    
    @Test
    public void testFindRestaurantsServingLocation() throws Exception {
        
        // Check for restaurants serving E18
        try {
            List<Restaurant> restaurants = repository.findRestaurantsServingPostCode("E18");
            Assert.assertEquals("Should return one restaurant",1,restaurants.size());
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
        }

    }
    
    
}
