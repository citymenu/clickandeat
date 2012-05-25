package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.*;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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
        restaurant.setName("Test Restaurant");
        
        Person mainContact = new Person();
        mainContact.setFirstName("test");
        mainContact.setLastName("owner");
        restaurant.setMainContact(mainContact);
        
        Address address = new Address();
        address.setPostCode("E18 2LG");
        restaurant.setAddress(address);

        restaurant.getCuisines().add("Mexican");
        restaurant.getCuisines().add("Chinese");
        
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
            repository.delete(restaurant);
        }
    }

    
    @Test
    public void testFindRestaurantsServingLocation() throws Exception {
        
        // Check for restaurants serving Mexican food in E18 ordered by name
        try {
            List<Restaurant> restaurants = repository.search("E18", "Mexican", "name", "asc");
            Assert.assertEquals("Should return one restaurant",1,restaurants.size());
            Restaurant restaurant = restaurants.get(0);
            Assert.assertEquals("Restaurant should be closed", RestaurantOpenStatus.CLOSED, restaurant.isOpen(new LocalDate(), new LocalTime()) );
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
        }

    }
    
    
}
