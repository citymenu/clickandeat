package com.ezar.clickandeat.util;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class CuisineProvider implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(CuisineProvider.class);
    
    private static final int LOCATION_LINK_SIZE = 25;

    
    @Autowired
    private RestaurantRepository restaurantRepository;

    private SortedSet<String> cuisineList = new TreeSet<String>();

    private List<Pair<String,String>> locations = new ArrayList<Pair<String, String>>();
    
    private Map<Pair<String,String>,List<Pair<String,String>>> cuisineLocations = new HashMap<Pair<String, String>, List<Pair<String, String>>>();

    private SortedSet<Pair<String,String>> locationPrimary = new TreeSet<Pair<String,String>>(new LocationNameComparator());
    
    private Map<Pair<String,String>,List<Pair<String,String>>> locationSecondary = new HashMap<Pair<String,String>, List<Pair<String,String>>>();
    
    private Map<String,String> cuisineMap = new HashMap<String,String>();
    
    @Override
    public void afterPropertiesSet() throws Exception {

        String[] cuisines = StringUtils.commaDelimitedListToStringArray(MessageFactory.getMessage("restaurants.cuisines", false));
        Collections.addAll(cuisineList, cuisines);

        Map<String,List<String>> cuisinesByLocation = restaurantRepository.getCuisinesByLocation();
        SortedSet<Pair<String,String>> locationNames = new TreeSet<Pair<String,String>>(new LocationNameComparator());

        // Extract top locations for footer list
        Map<String,Integer> locationCount = restaurantRepository.getRestaurantLocationCount();
        SortedSet<Pair<String,Integer>> locationsByCount = new TreeSet<Pair<String, Integer>>(new LocationCountComparator());
        for( Map.Entry<String,Integer> entry: locationCount.entrySet()) {
            locationsByCount.add(new Pair<String, Integer>(entry.getKey(), entry.getValue()));
        }
        int locationIndex = 0;
        for(Pair<String,Integer> count: locationsByCount) {
            if(locationIndex >= LOCATION_LINK_SIZE) {
                break;
            }
            String location = count.first;
            String escapedLocation = StringUtil.normalise(location).replace("\\","-").replaceAll(" ","-").toLowerCase(MessageFactory.getLocale());
            locations.add(new Pair<String, String>(escapedLocation,location));
            locationIndex++;
        }

        // Extract all cuisines for major links
        for( Map.Entry<String,List<String>> entry: cuisinesByLocation.entrySet()) {
            String location = entry.getKey();
            List<String> cuisineList = entry.getValue();
            String escapedLocation = StringUtil.normalise(location).replace("\\","-").replaceAll(" ","-").toLowerCase(MessageFactory.getLocale());
            locationNames.add(new Pair<String, String>(escapedLocation,location));
            Pair<String,String> locationPair = new Pair<String, String>(escapedLocation,location);
            List<Pair<String,String>> cuisinesPairList = new ArrayList<Pair<String, String>>();
            for( String cuisine: cuisineList ) {
                String escapedCuisine = StringUtil.normalise(cuisine).replace("\\","-").replaceAll(" ","-").toLowerCase(MessageFactory.getLocale());
                cuisineMap.put(escapedCuisine, cuisine);
                Pair<String,String> cuisinePair = new Pair<String, String>(escapedCuisine, cuisine);
                cuisinesPairList.add(cuisinePair);
            }
            cuisineLocations.put(locationPair,cuisinesPairList);
        }
        
        // Push all cuisines into the secondary links table
        List<Pair<String,String>> locationNamesList = new ArrayList<Pair<String, String>>();
        locationNamesList.addAll(locationNames);
        int index = 0;
        while(index < locationNamesList.size()) {
            List<Pair<String,String>> sublist = locationNamesList.subList(index,Math.min(index + LOCATION_LINK_SIZE, locationNames.size() -1 ));
            locationSecondary.put(sublist.get(0),sublist);
            index += LOCATION_LINK_SIZE;
        }
        
        // Now consolidate into the primary links table
        locationPrimary.clear();
        locationPrimary.addAll(locationSecondary.keySet());

        LOGGER.info("Loaded all cuisines into memory");
    }


    /**
     * @param locationPair
     * @return
     */

    public List<Pair<String,String>> getMappedLocations(Pair<String,String> locationPair) {
        List<Pair<String,String>> locations = locationSecondary.get(locationPair);
        return locations == null? new ArrayList<Pair<String, String>>(): locations;
    }

    
    public SortedSet<Pair<String, String>> getLocationPrimary() {
        return locationPrimary;
    }

    public List<Pair<String, String>> getLocations() {
        return locations;
    }

    public SortedSet<String> getCuisineList() {
        return cuisineList;
    }

    public Map<Pair<String, String>, List<Pair<String, String>>> getCuisineLocations() {
        return cuisineLocations;
    }

    public String getMappedCuisine(String escapedCuisine) {
        return cuisineMap.containsKey(escapedCuisine)? cuisineMap.get(escapedCuisine): escapedCuisine;
    }
    
    
    
    private static final class LocationCountComparator implements Comparator<Pair<String,Integer>> {
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
            return o2.second - o1.second;
        }
    }


    private static final class LocationNameComparator implements Comparator<Pair<String,String>> {
        public int compare(Pair<String, String> o1, Pair<String, String> o2) {
            return o1.first.compareTo(o2.first);
        }
    }

}
