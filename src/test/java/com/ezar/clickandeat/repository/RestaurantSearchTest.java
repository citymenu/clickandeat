package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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

import java.util.Arrays;
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
        
        Restaurant restaurant = repository.create();
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
        deliveryOptions.setDeliveryRadiusInKilometres(3d) ;
        deliveryOptions.getAreasDeliveredTo().add("E17");
        deliveryOptions.getAreasDeliveredTo().add("E6");
        restaurant.setDeliveryOptions(deliveryOptions);

        OpeningTimes openingTimes = new OpeningTimes();
        openingTimes.getClosedDates().add(new LocalDate(2012,12,1));
        restaurant.setOpeningTimes(openingTimes);
        
        repository.saveRestaurant(restaurant);
        LOGGER.debug("Saved restaurant");
        
    }

    
    @After
    public void tearDown() throws Exception {
        removeRestaurant(restaurantId);        
        // Let message listener clean up
        Thread.sleep(1000);
    }
    
    
    private void removeRestaurant(String restaurantId) throws Exception {
        Restaurant restaurant = repository.findByRestaurantId(restaurantId);
        if( restaurant != null ) {
            repository.deleteRestaurant(restaurant);
        }
    }

    
    @Test
    public void testFindRestaurantsServingLocation() throws Exception {
        
        // Check for restaurants serving Mexican food in E18 ordered by name
        try {
            Search search = new Search("E18", Arrays.asList("Mexican", "Italian"),"name","asc");
            List<Restaurant> restaurants = repository.search(search);
            Assert.assertEquals("Should return one restaurant",1,restaurants.size());
            Restaurant restaurant = restaurants.get(0);
            Assert.assertEquals("Restaurant should be closed", RestaurantOpenStatus.CLOSED, restaurant.isOpen(new DateTime()));
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
        }

    }
    
    
}
