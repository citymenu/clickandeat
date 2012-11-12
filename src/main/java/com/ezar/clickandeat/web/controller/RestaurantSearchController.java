package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class RestaurantSearchController {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantSearchController.class);

    @Autowired
    private GeoLocationService geoLocationService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;


    @RequestMapping(value="/**/loc/{address}/csn/{cuisine}", method = RequestMethod.GET)
    public ModelAndView searchByLocationAndCuisine(HttpServletRequest request, @PathVariable("address") String address, @PathVariable("cuisine") String cuisine ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Searching for restaurants");
        }

        try {
            GeoLocation geoLocation = null;
            if( StringUtils.hasText(address) ) {
                geoLocation = geoLocationService.getLocation(address);
                if( geoLocation == null ) {
                    LOGGER.warn("Could not resolve location for address: " + address);
                    Map<String,Object> model = new HashMap<String, Object>();
                    model.put("address", address);
                    model.put("notfound",true);
                    return new ModelAndView(MessageFactory.getLocaleString() + "/home",model);
                }
            }

            // Build new search session object
            HttpSession session = request.getSession(true);
            Search search = (Search)session.getAttribute("search"); 
            if( search == null ) {
                search = new Search();
            }
            if( geoLocation != null ) {
                search.setLocation(geoLocation);
            }
            search.setCuisine(cuisine);
            request.getSession(true).setAttribute("search", search);
            return search(search,address);
            
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            return new ModelAndView(MessageFactory.getLocaleString() + "/home",null);
        }

    }


    @RequestMapping(value="/**/loc/{address}", method = RequestMethod.GET)
    public ModelAndView searchByLocation(HttpServletRequest request, @PathVariable("address") String address ) {
        return searchByLocationAndCuisine(request,CuisineProvider.getFooterLocationMap().get(address),null);
    }


    @RequestMapping(value="/**/csn/{cuisine}", method = RequestMethod.GET)
    public ModelAndView searchByCuisine(HttpServletRequest request, @PathVariable("cuisine") String cuisine ) {
        return searchByLocationAndCuisine(request,null, CuisineProvider.getFooterCuisineMap().get(cuisine));
    }


    @RequestMapping(value="/**/session/loc", method = RequestMethod.GET)
    public ModelAndView savedSearch(HttpServletRequest request ) {
        Search search = (Search)request.getSession(true).getAttribute("search");
        if( search == null ) {
            return new ModelAndView(MessageFactory.getLocaleString() + "/home",null);
        }
        String address = search.getLocation() == null? null: search.getLocation().getAddress();
        return search(search, address);
    }


    @RequestMapping(value="/**/session/noloc", method = RequestMethod.GET)
    public ModelAndView savedSearchWithoutAddress(HttpServletRequest request ) {
        Search search = (Search)request.getSession(true).getAttribute("search");
        if( search == null ) {
            return new ModelAndView(MessageFactory.getLocaleString() + "/home",null);
        }
        return search(search, null);
    }

    
    @RequestMapping(value="/**/session/reloc", method = RequestMethod.GET)
    public ModelAndView savedSearchClearingCuisine(HttpServletRequest request ) {
        Search search = (Search)request.getSession(true).getAttribute("search");
        if( search == null ) {
            return new ModelAndView(MessageFactory.getLocaleString() + "/home",null);
        }
        search.setCuisine(null);
        String address = search.getLocation() == null? null: search.getLocation().getAddress();
        return search(search, address);
    }


    /**
     * @param search
     * @return
     */

    private ModelAndView search(Search search, String address) {

        Pair<List<Restaurant>,Map<String,Integer>> pair = restaurantRepository.search(search);
        SortedSet<Restaurant> results = new TreeSet<Restaurant>(new RestaurantSearchComparator());
        results.addAll(pair.first);

        Map<String,Object> model = new HashMap<String, Object>();
        model.put("address",address == null? "": address);
        model.put("cuisine",search.getCuisine() == null? "": search.getCuisine());
        model.put("results",results);
        model.put("count",results.size());
        model.put("cuisineCount",pair.second);

        // Put the system locale on the response
        return new ModelAndView("findRestaurant",model);
    }
    

    /**
     * Custom ordering for restaurant search results 
     */
    
    private static final class RestaurantSearchComparator implements Comparator<Restaurant> {

        @Override
        public int compare(Restaurant restaurant1, Restaurant restaurant2) {
            //First deal with those restaurants that support phone orders only
            if( !restaurant1.getPhoneOrdersOnly() && restaurant2.getPhoneOrdersOnly()) {
                return -1;
            }
            else if( restaurant1.getPhoneOrdersOnly() && !restaurant2.getPhoneOrdersOnly()) {
                return 1;
            }

            if( restaurant1.getOpen() && !restaurant2.getOpen()) {
                return -1;                
            }
            else if( !restaurant1.getOpen() && restaurant2.getOpen()) {
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
