package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.model.ValueObject;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private CuisineProvider cuisineProvider;


    @RequestMapping(value="/*/loc/{address}", method = RequestMethod.GET)
    public ModelAndView search(HttpServletRequest request, @PathVariable("address") String address ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Searching for restaurants");
        }

        try {
            GeoLocation geoLocation = geoLocationService.getLocation(address);
            if( geoLocation == null ) {
                LOGGER.warn("Could not resolve location for address: " + address);
                return new ModelAndView(MessageFactory.getLocaleString() + "/home",null);
            }

            Search search = new Search();
            search.setLocation(geoLocation);
            request.getSession(true).setAttribute("search", search);
    
            return buildSearchResults(search);

        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            return new ModelAndView(MessageFactory.getLocaleString() + "/home",null);
        }
    }


    @RequestMapping(value="/*/session/loc", method = RequestMethod.GET)
    public ModelAndView savedSearch(HttpServletRequest request ) {

        Search search = (Search)request.getSession(true).getAttribute("search");
        if( search == null ) {
            return new ModelAndView(MessageFactory.getLocaleString() + "/home",null);
        }
        return buildSearchResults(search);
    }


    @RequestMapping(value="/**/browse/loc/${location}", method = RequestMethod.GET)
    public ModelAndView getCuisineCountByLocation(HttpServletRequest request, @PathVariable("location") String location ) {
        
        Map<String,Object> model = new HashMap<String, Object>();
        Map<String,Integer> cuisineCount = restaurantRepository.getCuisineCountByLocation(location);
        model.put("location",location);
        model.put("cuisineCount",cuisineCount);
        model.put("type","browse");
        return new ModelAndView("findRestaurant",model);
    }
    
    
    /**
     * @param search
     * @return
     */
    
    private ModelAndView buildSearchResults(Search search) {

        Map<String,Object> model = new HashMap<String,Object>();
        
        SortedSet<Restaurant> results = new TreeSet<Restaurant>(new RestaurantSearchComparator());
        results.addAll(restaurantRepository.search(search));
        model.put("results",results);
        model.put("count",results.size());

        // Put the system locale on the response
        model.put("resultCount", buildCuisineResultCount(results));
        model.put("cuisines",cuisineProvider.getCuisineList());
        model.put("type","search");
        return new ModelAndView("findRestaurant",model);

    }
    

    /**
     * @param results
     * @return
     */
    
    private SortedSet<CuisineCount> buildCuisineResultCount(Set<Restaurant> results) {
        Map<String,Integer> resultMap = new HashMap<String, Integer>();
        for( Restaurant restaurant: results ) {
            for( String cuisine: restaurant.getCuisines()) {
                Integer resultCount = resultMap.get(cuisine);
                if( resultCount == null ) {
                    resultCount = 0;
                }
                resultMap.put(cuisine, resultCount + 1 );
            }
        }
        SortedSet<CuisineCount> ret = new TreeSet<CuisineCount>();
        for( Map.Entry<String,Integer> entry: resultMap.entrySet()) {
            ret.add(new CuisineCount(entry.getKey(), entry.getValue()));
        }
        return ret;
    }

    /**
     * Ordering of cuisine counts
     */
    
    public static final class CuisineCount implements Comparable<CuisineCount> {
        
        final String cuisine;
        final Integer count;

        public CuisineCount(String cuisine, Integer count) {
            this.cuisine = cuisine;
            this.count = count;
        }

        @Override
        public int compareTo(CuisineCount o) {
            return cuisine.compareTo(o.cuisine);
        }

        public String getCuisine() {
            return cuisine;
        }

        public int getCount() {
            return count;
        }
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
