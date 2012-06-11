package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import flexjson.JSONSerializer;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class RestaurantController {

    private static final Logger LOGGER = Logger.getLogger(RestaurantController.class);
    
    @Autowired
    private RestaurantRepository repository;

    @Autowired
    private CuisineProvider cuisineProvider;
    
    private final JSONSerializer serializer = new JSONSerializer();
    
    @ResponseBody
    @RequestMapping(value="/admin/restaurants/list.ajax", method = RequestMethod.GET )
    public ResponseEntity<byte[]> list(@RequestParam(value = "page") int page, @RequestParam(value = "start") int start,
                                       @RequestParam(value = "limit") int limit ) throws Exception {

        PageRequest request = new PageRequest(page - 1, limit - start );
        Page<Restaurant> restaurants = repository.findAll(request);

        Map<String,Object> model = new HashMap<String,Object>();
        model.put("restaurants",restaurants.getContent());
        model.put("count",repository.count());
        String json = serializer.deepSerialize(model);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    @RequestMapping(value="/admin/restaurants/create.html", method = RequestMethod.GET )
    public ModelAndView create() {
        Map<String,Object> model = getModel();
        Restaurant restaurant = new Restaurant();
        restaurant.setId("DUMMY");
        restaurant.setName("Test Restaurant");
        restaurant.getCuisines().add("Italian");
        restaurant.getCuisines().add("Pizza");
        
        Address address = new Address();
        address.setAddress1("80 Peel Road");
        address.setPostCode("E18");
        restaurant.setAddress(address);

        Person mainContact = new Person();
        mainContact.setFirstName("Joe");
        mainContact.setLastName("Pugh");
        restaurant.setMainContact(mainContact);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setDeliveryOptionsSummary("Summary");
        deliveryOptions.setMinimumOrderForCollectionDiscount(10d);
        deliveryOptions.setDeliveryCharge(5.5);
        deliveryOptions.setDeliveryRadiusInKilometres(2d);
        deliveryOptions.getAreasDeliveredTo().add("E18");
        deliveryOptions.getAreasDeliveredTo().add("E17");
        restaurant.setDeliveryOptions(deliveryOptions);

        OpeningTimes openingTimes = new OpeningTimes();
        openingTimes.setOpeningTimesSummary("Opening summary");
        openingTimes.getClosedDates().add(new LocalDate(2012,12,24));
        openingTimes.getClosedDates().add(new LocalDate(2013,12,24));
        
        OpeningTime openingTime1 = new OpeningTime();
        openingTime1.setOpen(true);
        openingTime1.setDayOfWeek(1);
        openingTime1.setCollectionOpeningTime(new LocalTime(15,0));
        openingTime1.setCollectionClosingTime(new LocalTime(23,30));
        openingTime1.setDeliveryOpeningTime(new LocalTime(3,15));
        openingTime1.setDeliveryClosingTime(new LocalTime(23,0));
        openingTimes.getOpeningTimes().add(openingTime1);
        restaurant.setOpeningTimes(openingTimes);

        Menu menu = new Menu();
        
        MenuCategory category1 = new MenuCategory();
        category1.setName("Starters");
        category1.setSummary("Enjoy these starters");
        category1.setType(MenuCategory.TYPE_STANDARD);
        
        MenuItem item1 = new MenuItem();
        item1.setNumber(1);
        item1.setTitle("Onion Hhaji");
        item1.setSubtitle("Nice onion bhajii");
        item1.setCost(100d);
        item1.setDescription("Its a really nice bit of food");
        category1.getMenuItems().add(item1);

        MenuItem item2 = new MenuItem();
        item2.setNumber(2);
        item2.setTitle("Cheese");
        item2.setSubtitle("Nice cheese");
        item2.setCost(7.45d);
        item2.setDescription("Smelly cheese");
        category1.getMenuItems().add(item2);

        MenuCategory category2 = new MenuCategory();
        category2.setName("Mains");
        category2.setSummary("Enjoy these mains");
        category2.setType("Ordinary");

        MenuItem item3 = new MenuItem();
        item3.setNumber(3);
        item3.setTitle("Pizza");
        item3.setSubtitle("Stuff");
        item3.setCost(10.4d);
        item3.setDescription("Big fat pizza");
        category2.getMenuItems().add(item3);

        menu.getMenuCategories().add(category1);
        menu.getMenuCategories().add(category2);

        restaurant.setMenu(menu);
        
        NotificationOptions notificationOptions = new NotificationOptions();
        notificationOptions.setReceiveNotificationCall(true);
        notificationOptions.setReceiveSMSNotification(true);
        notificationOptions.setTakeOrderOverTelephone(true);
        notificationOptions.setNotificationEmailAddress("joe.pugh@db.com");
        notificationOptions.setNotificationPhoneNumber("888");
        notificationOptions.setNotificationSMSNumber("777");
        restaurant.setNotificationOptions(notificationOptions);
        
        model.put("restaurant", restaurant);
        model.put("json",Restaurant.toJSON(restaurant));
        return new ModelAndView("admin/editRestaurant",model);
    }


    @RequestMapping(value="/admin/restaurants/edit.html", method = RequestMethod.GET )
    public ModelAndView edit(@RequestParam(value = "restaurantId") String restaurantId) {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing restaurant with id [" + restaurantId + "]");
        }
        
        Map<String,Object> model = getModel();
        Restaurant restaurant = repository.findByRestaurantId(restaurantId);
        model.put("restaurant",restaurant);
        model.put("json",Restaurant.toJSON(restaurant));
        return new ModelAndView("admin/editRestaurant",model);
    }


    @ResponseBody
    @RequestMapping(value="/admin/restaurants/save.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> save(@RequestParam(value = "body") String body) throws Exception {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating restaurant");
        }
        
        Map<String,Object> model = new HashMap<String, Object>();
        try {
            Restaurant restaurant = Restaurant.fromJSON(body);
            restaurant = repository.saveRestaurant(restaurant);
            model.put("success",true);
            model.put("id",restaurant.getId());
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        String json = serializer.deepSerialize(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }
 

    /**
     * Returns standard model
     * @return
     */
    
    private Map<String,Object> getModel() {
        Map<String,Object> model = new HashMap<String, Object>();
        Set<String> cuisines = cuisineProvider.getCuisineList();
        String cuisineArrayList = StringUtils.collectionToDelimitedString(cuisines,"','");
        model.put("cuisinesArray","'" + cuisineArrayList + "'");
        return model;
    }
    
}
