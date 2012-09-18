package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.AddressLocation;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.repository.AddressLocationRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class RestaurantSearchController {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantSearchController.class);

    @Autowired
    private LocationService locationService;

    @Autowired
    private AddressLocationRepository addressLocationRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    @Autowired
    private CuisineProvider cuisineProvider;


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/validateLocation.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> list(@RequestParam(value = "loc") String loc ) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();
        
        try {
            List<AddressLocation> locations = new ArrayList<AddressLocation>();
            AddressLocation addressLocation = addressLocationRepository.findByAddress(loc);
            if( addressLocation != null ) {
                locations.add(addressLocation);
            }
            else {
                locations.addAll(locationService.getLocations(loc));
            }
            model.put("locations",locations);
            model.put("success",true);
        }
        catch( Exception ex ) {
            model.put("success",false);
        }
        return responseEntityUtils.buildResponse(model);
    }


    @RequestMapping(value="/findRestaurant.html", method = RequestMethod.GET)
    public ModelAndView search(@RequestParam(value = "loc", required = false) String location, @RequestParam(value = "c", required = false ) List<String> cuisines,
                                        @RequestParam(value = "s", required = false) String sort, @RequestParam(value = "d", required = false) String dir,
                                        HttpServletRequest request) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Searching for restaurants serving location: " + location);
        }

        Map<String,Object> model = new HashMap<String,Object>();

        AddressLocation addressLocation = locationService.getSingleLocation(location);
        if( addressLocation == null || addressLocation.isRadiusInvalid()) {
            return new ModelAndView("redirect:/home.html",model);
        }

        Search search = new Search(addressLocation,cuisines,sort,dir);
        SortedSet<Restaurant> results = new TreeSet<Restaurant>(new RestaurantSearchComparator());
        results.addAll(restaurantRepository.search(search));
        model.put("results",results);
        model.put("count",results.size());
        model.put("cuisines",cuisineProvider.getCuisineList());

        // Store search in session
        request.getSession(true).setAttribute("search",search);

        return new ModelAndView("findRestaurant",model);
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
