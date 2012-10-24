package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.model.Search;
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
import java.util.HashMap;
import java.util.Map;

@Controller
public class LocationValidationController {
    
    private static final Logger LOGGER = Logger.getLogger(LocationValidationController.class);

    @Autowired
    private GeoLocationService geoLocationService;
    
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
                request.getSession(true).setAttribute("search", search);
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
            Search search = new Search();
            search.setLocation(StringUtils.hasText(address)? geoLocationService.getLocation(address): null);
            search.setCuisine(StringUtils.hasText(cuisine)? cuisine: null);
            request.getSession(true).setAttribute("search", search);
            model.put("success",true);
        }
        catch( Exception ex ) {
            model.put("success",false);
        }
        return buildResponse(model);
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
