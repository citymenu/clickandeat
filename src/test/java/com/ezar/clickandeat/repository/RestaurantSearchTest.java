package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.util.Pair;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class RestaurantSearchTest {

    private static final Logger LOGGER = Logger.getLogger(RestaurantSearchTest.class);
    
    @Autowired
    private RestaurantRepository repository;

    @Autowired
    private GeoLocationService locationService;

    private String restaurantId = "testrestaurant";

    @Before
    public void setup() throws Exception {

        removeRestaurant(restaurantId);
        
        Restaurant restaurant = repository.create();
        restaurant.setRestaurantId(restaurantId);
        restaurant.setName("Test Restaurant");
        restaurant.setListOnSite(true);
        
        Person mainContact = new Person();
        mainContact.setFirstName("test");
        mainContact.setLastName("owner");
        restaurant.setMainContact(mainContact);
        
        Address address = new Address();
        address.setPostCode("E18 2LG");
        address.setTown("London");
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
    }
    
    
    private void removeRestaurant(String restaurantId) throws Exception {
        Restaurant restaurant = repository.findByRestaurantId(restaurantId);
        if( restaurant != null ) {
            repository.deleteRestaurant(restaurant);
        }
    }


    @Test
    public void testGetCuisineCountByLocation() throws Exception {
        Search search = new Search();
        search.setLocation(locationService.getLocation("London"));
        search.setCuisine("Mexican");
        Pair<List<Restaurant>,Map<String,Integer>> pair = repository.search(search);
        List<Restaurant> restaurants = pair.first;
        Map<String,Integer> cuisineCount = pair.second;
        Assert.assertTrue(restaurants.size() > 0);
        LOGGER.info("Found " + restaurants.size() + " restaurants");
    }

    
    @Test
    public void testGetCuisinesByLocation() throws Exception {
        Map<String,List<String>> cuisinesByLocation = repository.getCuisinesByLocation();
        Assert.assertTrue(cuisinesByLocation.size() > 0);
    }

    
}
