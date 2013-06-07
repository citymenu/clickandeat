package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.util.StringUtil;
import com.ezar.clickandeat.web.controller.helper.RestaurantSearchComparator;
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
    private OrderRepository orderRepository;
    
    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    @Autowired
    private CuisineProvider cuisineProvider;

    
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
                    return new ModelAndView("redirect:/home.html");
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
                
                // If there is an order associated with the session, update the delivery address
                String orderId = (String)session.getAttribute("orderid");
                if( orderId != null ) {
                    Order order = orderRepository.findByOrderId(orderId);
                    if( order != null ) {
                        order.setDeliveryAddress(geoLocationService.buildAddress(geoLocation));
                        orderRepository.saveOrder(order);
                    }
                }
            }
            search.setCuisine(cuisine);
            session.setAttribute("search", search);
            session.setAttribute("searchurl", request.getRequestURL().toString());
            return search(search,address);
            
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            return new ModelAndView("redirect:/home.html");
        }

    }


    @RequestMapping(value="/**/loc/{address}", method = RequestMethod.GET)
    public ModelAndView searchByLocation(HttpServletRequest request, @PathVariable("address") String address ) {
        String unescapedLocation = cuisineProvider.getMappedLocation(address);
        return searchByLocationAndCuisine(request,unescapedLocation,null);
    }


    @RequestMapping(value="/**/csn/{cuisine}/{address}", method = RequestMethod.GET)
    public ModelAndView searchByCuisine(HttpServletRequest request, @PathVariable("cuisine") String cuisine, @PathVariable("address") String address ) {
        String unescapedLocation = cuisineProvider.getMappedLocation(address);
        String unescapedCuisine = cuisineProvider.getMappedCuisine(cuisine);
        return searchByLocationAndCuisine(request, unescapedLocation, unescapedCuisine );
    }


    @RequestMapping(value="/**/session/loc", method = RequestMethod.GET)
    public ModelAndView savedSearch(HttpServletRequest request ) {
        Search search = (Search)request.getSession(true).getAttribute("search");
        if( search == null ) {
            return new ModelAndView("redirect:/home.html");
        }
        String address = search.getLocation() == null? null: search.getLocation().getAddress();
        return search(search, address);
    }


    @RequestMapping(value="/**/session/noloc", method = RequestMethod.GET)
    public ModelAndView savedSearchWithoutAddress(HttpServletRequest request ) {
        Search search = (Search)request.getSession(true).getAttribute("search");
        if( search == null ) {
            return new ModelAndView("redirect:/home.html");
        }
        return search(search, null);
    }

    
    @RequestMapping(value="/**/session/reloc", method = RequestMethod.GET)
    public ModelAndView savedSearchClearingCuisine(HttpServletRequest request ) {
        Search search = (Search)request.getSession(true).getAttribute("search");
        if( search == null ) {
            return new ModelAndView("redirect:/home.html");
        }
        search.setCuisine(null);
        String address = search.getLocation() == null? null: search.getLocation().getAddress();
        
        HttpSession session = request.getSession(true);
        session.removeAttribute("searchurl");
        
        return search(search, address);
    }


    /**
     * @param search
     * @return
     */

    private ModelAndView search(Search search, String address) {

        Pair<List<Restaurant>,SortedMap<String,Integer>> pair = restaurantRepository.search(search);
        SortedSet<Restaurant> results = new TreeSet<Restaurant>(new RestaurantSearchComparator());
        results.addAll(pair.first);

        // Build direct links to all cuisines at this location
        List<Pair<String,String>> cuisineLinks = new ArrayList<Pair<String, String>>();
        for(String cuisine: pair.second.keySet()) {
            String escapedCuisine = StringUtil.normalise(cuisine).replace("\\","-").replaceAll(" ","-").toLowerCase(MessageFactory.getLocale());
            cuisineLinks.add(new Pair<String, String>(escapedCuisine,cuisine));
        }
        String escapedAddress = StringUtil.normalise(address).replace("\\","-").replaceAll(" ","-").toLowerCase(MessageFactory.getLocale());
        Pair<String,String> locationLinks = new Pair<String, String>(escapedAddress, address);

        Map<String,Object> model = new HashMap<String, Object>();
        model.put("address",address == null? "": address);
        model.put("cuisine",search.getCuisine() == null? "": search.getCuisine());
        model.put("results",results);
        model.put("count",results.size());
        model.put("cuisineCount",pair.second);

        model.put("cuisineLinks",cuisineLinks);
        model.put("locationLinks",locationLinks);

        // Add any secondary key links
        model.put("secondarylinks",cuisineProvider.getMappedLocations(locationLinks));

        // Put the system locale on the response
        return new ModelAndView("findRestaurant",model);
    }

}
