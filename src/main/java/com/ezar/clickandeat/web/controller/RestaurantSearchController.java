package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

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

        SortedSet<Restaurant> results = new TreeSet<Restaurant>(new RestaurantSearchComparator());
        results.addAll(restaurantRepository.search(location, cuisine, sort, dir ));
        model.put("results",results);
        model.put("count",results.size());

        return new ModelAndView("results",model);
    }

    
    /**
     * Custom ordering for restaurant search results 
     */
    
    private static final class RestaurantSearchComparator implements Comparator<Restaurant> {

        @Override
        public int compare(Restaurant restaurant1, Restaurant restaurant2) {
            if( restaurant1.isOpenForDelivery() && !restaurant2.isOpenForDelivery()) {
                return -1;                
            }
            else if( !restaurant1.isOpenForDelivery() && restaurant2.isOpenForDelivery()) {
                return 1;
            }
            else {
                double distanceDiff = restaurant1.getDistanceToSearchLocation() - restaurant2.getDistanceToSearchLocation();
                if( distanceDiff == 0 ) {
                    return restaurant1.getName().compareTo(restaurant2.getName());
                }
                else if( distanceDiff < 0 ) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        }
    }
    
}
