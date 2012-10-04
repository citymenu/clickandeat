package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.maps.PlacesService;
import com.ezar.clickandeat.util.JSONUtils;
import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PlacesController {

    @Autowired
    private PlacesService placesService;

    @Autowired
    private JSONUtils jsonUtils;

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/locationLookup.html", method = RequestMethod.POST )
    public ResponseEntity<byte[]> locationLookup(@RequestParam(value="query") String query) throws Exception {
        List<String> predictions = placesService.getAddresses(query);
        Map<String,List<String>> model = new HashMap<String, List<String>>();
        model.put("options",predictions);
        String json = jsonUtils.serializeAndEscape(model);
        String escaped = jsonUtils.escapeQuotes(json);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(escaped.getBytes("utf-8"), headers, HttpStatus.OK);
    }

}
