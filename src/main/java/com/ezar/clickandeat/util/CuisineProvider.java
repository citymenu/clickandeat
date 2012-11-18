package com.ezar.clickandeat.util;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class CuisineProvider implements InitializingBean {

    @Autowired
    private RestaurantRepository restaurantRepository;
    
    private SortedSet<String> cuisineList = new TreeSet<String>();

    private Map<Pair<String,String>,List<Pair<String,String>>> cuisineLocations = new HashMap<Pair<String, String>, List<Pair<String, String>>>();

    
    @Override
    public void afterPropertiesSet() throws Exception {
        
        String[] cuisines = StringUtils.commaDelimitedListToStringArray(MessageFactory.getMessage("restaurants.cuisines", false));
        Collections.addAll(cuisineList, cuisines);
        
        Map<String,List<String>> cuisinesByLocation = restaurantRepository.getCuisinesByLocation();

        for( Map.Entry<String,List<String>> entry: cuisinesByLocation.entrySet()) {
            String location = entry.getKey();
            String escapedLocation = StringEscapeUtils.escapeJavaScript(location).replace("\\","-");
            List<String> cuisineList = entry.getValue();
            Pair<String,String> locationPair = new Pair<String, String>(escapedLocation,location);
            List<Pair<String,String>> cuisinesPairList = new ArrayList<Pair<String, String>>();
            for( String cuisine: cuisineList ) {
                String escapedCuisine = StringEscapeUtils.escapeJavaScript(cuisine).replace("\\","-");
                Pair<String,String> cuisinePair = new Pair<String, String>(escapedCuisine, cuisine);
                cuisinesPairList.add(cuisinePair);
            }
            cuisineLocations.put(locationPair, cuisinesPairList);
        }
    }

    
    public SortedSet<String> getCuisineList() {
        return cuisineList;
    }

    public Map<Pair<String, String>, List<Pair<String, String>>> getCuisineLocations() {
        return cuisineLocations;
    }
}
