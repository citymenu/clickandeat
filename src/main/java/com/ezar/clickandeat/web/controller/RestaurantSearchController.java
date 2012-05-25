package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.RestaurantOpenStatus;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RestaurantSearchController {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantSearchController.class);
    
    @Autowired
    private RestaurantRepository restaurantRepository;


    @RequestMapping(value="/search.html", method = RequestMethod.GET)
    public ModelAndView search(@RequestParam(value = "loc", required = false) String location, @RequestParam(value = "c", required = false ) String cuisine,
                                        @RequestParam(value = "s", required = false) String sort, @RequestParam(value = "d", required = false) String dir ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Searching for restaurants serving location: " + location);
        }

        Map<String,Object> model = new HashMap<String,Object>();

        List<Restaurant> fullyOpen = new ArrayList<Restaurant>();
        List<Restaurant> openForCollection = new ArrayList<Restaurant>();
        List<Restaurant> closed = new ArrayList<Restaurant>();

        LocalDate today = new LocalDate();
        LocalTime now = new LocalTime();
        
        for( Restaurant restaurant: restaurantRepository.search(location, cuisine, sort, dir )) {

            // Confirm if the restaurant is open for delivery or collection
            RestaurantOpenStatus isOpen = restaurant.isOpen(today,now);
            if( isOpen.equals(RestaurantOpenStatus.FULLY_OPEN)) {
                fullyOpen.add(restaurant);
            }
            else if( isOpen.equals(RestaurantOpenStatus.OPEN_FOR_COLLECTION)) {
                openForCollection.add(restaurant);
            }
            else {
                closed.add(restaurant);
            }
        }

        model.put("fullyOpen",fullyOpen);
        model.put("openForCollection",openForCollection);
        model.put("closed",closed);
        
        return new ModelAndView("results",model);
    }

}
