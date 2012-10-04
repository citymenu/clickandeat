package com.ezar.clickandeat.maps;

import flexjson.JSONDeserializer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PlacesService {

    private static final String MAP_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key={0}&input={1}&types=geocode&components=country:{2}&language={3}&sensor=false";

    private String apiKey;

    private String locale;

    private String country;

    
    public List<String> getAddresses(String search) throws Exception {
        List<String> addresses = new ArrayList<String>();
        URL url = new URL(MessageFormat.format(MAP_URL, apiKey, URLEncoder.encode(search, "UTF-8"), country, locale));
        URLConnection conn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        Map<String,Object> json = (Map<String,Object>)new JSONDeserializer().deserialize(in);
        List<Map<String,Object>> predictions = (List<Map<String,Object>>)json.get("predictions");
        for( Map<String,Object> prediction: predictions ) {
            addresses.add((String)prediction.get("description"));
        }
        return addresses;
    }


    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale.split("_")[0];
        this.country = locale.split("_")[1];
    }


    @Required
    @Value(value="${location.apiKey}")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
