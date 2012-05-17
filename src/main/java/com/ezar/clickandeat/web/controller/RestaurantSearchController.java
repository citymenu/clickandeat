package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Restaurant;
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
    public ModelAndView search(@RequestParam("loc") String location, @RequestParam("c") String cuisine,
                                        @RequestParam("s") String sort, @RequestParam("d") String dir ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Searching for restaurants serving location: " + location);
        }
        
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("results",restaurantRepository.search(location, cuisine, sort, dir ));
        return new ModelAndView("results",model);
    }

}
