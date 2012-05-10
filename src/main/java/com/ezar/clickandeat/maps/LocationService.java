package com.ezar.clickandeat.maps;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Component(value = "locationService")
public class LocationService {
    
    private static final Logger LOGGER = Logger.getLogger(LocationService.class);

    private static final String MAP_URL = "http://maps.googleapis.com/maps/api/geocode/json?address={0}&sensor=false&region={1}";
    
    /**
     * Gets the location of a postcode from the google maps api
     * @param postCode
     * @param region
     * @return
     */
    @SuppressWarnings("unchecked")
    public double[] getLocation(String postCode, String region ) {
        
        double[] ret = new double[2];
        
        try {
            URL url = new URL(MessageFormat.format(MAP_URL, URLEncoder.encode(postCode, "UTF-8"),region));
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Map<String,Object> json = (Map<String,Object>)new JSONDeserializer().deserialize(in);
            List<Map<String,Object>> results = (List<Map<String,Object>>)json.get("results");
            Map<String,Object> result = results.get(0);
            Map<String,Object> geometry = (Map<String,Object>)result.get("geometry");
            Map<String,Object> location = (Map<String,Object>)geometry.get("location");
            ret[0] = (Double)location.get("lng");
            ret[1] = (Double)location.get("lat");
            return ret;
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            return ret;
        }
        
    }
}
