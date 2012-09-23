package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.maps.LocationService;
import com.ezar.clickandeat.model.AddressLocation;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.validator.AddressValidator;
import com.ezar.clickandeat.validator.ValidationErrors;
import org.apache.commons.lang.StringEscapeUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    
    private static final Logger LOGGER = Logger.getLogger(SearchController.class);

    @Autowired
    private AddressValidator addressValidator;

    @Autowired
    private LocationService locationService;
    
    @Autowired
    private JSONUtils jsonUtils;

    
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/validateLocation.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> validateLocation(@RequestParam(value = "loc", required = false) String location, @RequestParam(value = "c", required = false ) List<String> cuisines,
                                                   @RequestParam(value = "s", required = false) String sort, @RequestParam(value = "d", required = false) String dir,
                                                   HttpServletRequest request) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            String address = StringEscapeUtils.unescapeHtml(location).replace("###","'");
            ValidationErrors errors = addressValidator.validate(address);
            if( errors.hasErrors()) {
                model.put("success",true);
                model.put("valid",false);
            } else {
                model.put("valid",true);
                List<AddressLocation> locations = locationService.getLocations(address);
                if( locations.size() == 1 ) {
                    Search search = new Search();
                    search.setLocation(locations.get(0));
                    search.setCuisines(cuisines);
                    search.setDir(dir);
                    search.setSort(sort);
                    request.getSession(true).setAttribute("search",search);
                    model.put("success",true);
                    model.put("exactMatch",true);
                }
                else {
                    List<String> locationNames = new ArrayList<String>();
                    for( AddressLocation addressLocation: locations) {
                        locationNames.add(addressLocation.getDisplayAddress());
                    }
                    model.put("success",true);
                    model.put("locations",locationNames);
                    model.put("exactMatch",false);
                }
            }
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