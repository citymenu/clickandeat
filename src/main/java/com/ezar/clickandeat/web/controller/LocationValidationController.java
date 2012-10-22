package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.util.JSONUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public ResponseEntity<byte[]> validateLocation(@RequestParam(value = "loc", required = false) String address) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Validating address: " + address);
        }

        try {
            GeoLocation geoLocation = geoLocationService.getLocation(address);
            model.put("success", geoLocation != null );
        }
        catch( Exception ex ) {
            model.put("success",false);
        }

        String escaped = jsonUtils.serializeAndEscape(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(escaped.getBytes("utf-8"), headers, HttpStatus.OK);
    }



}
