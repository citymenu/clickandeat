package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.JSONUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LocationValidationController {
    
    private static final Logger LOGGER = Logger.getLogger(LocationValidationController.class);

    @Autowired
    private GeoLocationService geoLocationService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private JSONUtils jsonUtils;

    
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/validateLocation.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> validateLocation(@RequestParam(value = "loc", required = false) String address, @RequestParam(value = "c", required = false ) String cuisine,
                                                   HttpServletRequest request) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            GeoLocation geoLocation = geoLocationService.getLocation(address);
            if( geoLocation == null ) {
                model.put("success",false);
            }
            else {
                Search search = new Search();
                search.setLocation(geoLocation);
                search.setCuisine(cuisine);

                HttpSession session = request.getSession(true);
                session.setAttribute("search", search);
                session.removeAttribute("searchurl");

                // If there is an order in the session, update the delivery address
                updateOrderDeliveryAddress(session,search);
                model.put("success",true);
            }
        }
        catch( Exception ex ) {
            model.put("success",false);
        }
        return buildResponse(model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/updateLocation.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> updateLocation(@RequestParam(value = "loc", required = false) String address, @RequestParam(value = "c", required = false ) String cuisine,
                                           HttpServletRequest request) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();

        try {
            HttpSession session = request.getSession(true);
            Search search = (Search)session.getAttribute("search");
            if( search == null ) {
                search = new Search();
            }
            if( StringUtils.hasText(address)) {
                search.setLocation(geoLocationService.getLocation(address));
                // If there is an order in the session, update the delivery address
                updateOrderDeliveryAddress(session,search);
            }
            search.setCuisine(StringUtils.hasText(cuisine)? cuisine: null);
            session.setAttribute("search", search);
            session.removeAttribute("searchurl");
            model.put("success",true);
        }
        catch( Exception ex ) {
            model.put("success",false);
        }
        return buildResponse(model);
    }


    /**
     * @param session
     * @param search
     */
    
    private void updateOrderDeliveryAddress(HttpSession session, Search search) {
        if( search == null || search.getLocation() == null ) {
            return;
        }
        String orderId = (String)session.getAttribute("orderid");
        if( orderId != null ) {
            Order order = orderRepository.findByOrderId(orderId);
            if( order != null ) {
                order.setDeliveryAddress(geoLocationService.buildAddress(search.getLocation()));
                orderRepository.saveOrder(order);
            }
            else {
                session.removeAttribute("orderid");
            }
        }
    }
    
    
    /**
     * @param model
     * @return
     * @throws Exception
     */

    private ResponseEntity<byte[]> buildResponse(Map<String,Object> model) throws Exception {
        String escaped = jsonUtils.serializeAndEscape(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(escaped.getBytes("utf-8"), headers, HttpStatus.OK);
    }

    
}
