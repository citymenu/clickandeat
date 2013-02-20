package com.ezar.clickandeat.web.controller.mobile;

import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.web.controller.helper.RestaurantSearchComparator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MobileRestaurantSearchController {
    
    private static final Logger LOGGER = Logger.getLogger(MobileRestaurantSearchController.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    private final String[] excludes = new String[]{"restaurants.menu","restaurants.specialOffers","restaurants.discounts",
            "restaurants.firstDiscount","restaurants.openingTimes"};


    @ResponseBody
    @RequestMapping(value="/mobile/restaurants/geolocationsearch.ajax", method = RequestMethod.GET)
    public ResponseEntity<byte[]> geoLocationSearch(@RequestParam(value="longitude") Double longitude, @RequestParam(value="latitude") Double latitude ) throws Exception {

        Map<String,Object> model = new HashMap<String,Object>();
        
        try {
            GeoLocation geoLocation = new GeoLocation();
            geoLocation.setLocation(new double[]{longitude,latitude});
            Search search = new Search();
            search.setLocation(geoLocation);
            Pair<List<Restaurant>,SortedMap<String,Integer>> pair = restaurantRepository.search(search);
            SortedSet<Restaurant> results = new TreeSet<Restaurant>(new RestaurantSearchComparator());
            results.addAll(pair.first);
            
            model.put("success",true);
            model.put("count",results.size());
            model.put("restaurants",results);

        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        return responseEntityUtils.buildResponse(model,excludes);
    }
    
}
