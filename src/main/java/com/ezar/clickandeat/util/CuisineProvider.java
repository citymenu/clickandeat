package com.ezar.clickandeat.util;

import com.ezar.clickandeat.config.MessageFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

public class CuisineProvider {

    private static final SortedSet<String> cuisineList;

    private static final SortedMap<String,String> footerCuisineMap;

    private static final SortedMap<String,String> footerLocationMap;
   
    
    
    static {
        String[] cuisines = StringUtils.commaDelimitedListToStringArray(MessageFactory.getMessage("restaurants.cuisines", false));
        cuisineList = new TreeSet<String>();
        Collections.addAll(cuisineList, cuisines);

        String[] footerCuisines = StringUtils.commaDelimitedListToStringArray(MessageFactory.getMessage("restaurants.footerCuisines", false));
        String[] footerCuisineKeys = StringUtils.commaDelimitedListToStringArray(MessageFactory.getMessage("restaurants.footerCuisineKeys", false));
        String[] footerLocations = StringUtils.commaDelimitedListToStringArray(MessageFactory.getMessage("restaurants.footerLocations", false));
        String[] footerLocationKeys = StringUtils.commaDelimitedListToStringArray(MessageFactory.getMessage("restaurants.footerLocationKeys", false));

        Assert.isTrue(footerCuisines.length == footerCuisineKeys.length, "restaurant.footerCuisines and restaurants.footerCuisineKeys must have matching number of entries");
        Assert.isTrue(footerLocations.length == footerLocationKeys.length, "restaurant.footerLocations and restaurants.footerLocationKeys must have matching number of entries");

        footerCuisineMap = new TreeMap<String, String>();
        for( int i = 0; i < footerCuisines.length; i++ ) {
            footerCuisineMap.put(footerCuisineKeys[i],footerCuisines[i]);
        }

        footerLocationMap = new TreeMap<String, String>();
        for( int i = 0; i < footerLocations.length; i++ ) {
            footerLocationMap.put(footerLocationKeys[i],footerLocations[i]);
        }
    }
    
    
    public static SortedSet<String> getCuisineList() {
        return cuisineList;
    }
    
    
    public static SortedMap<String,String> getFooterCuisineMap() {
        return footerCuisineMap;
    }

    public static SortedMap<String,String> getFooterLocationMap() {
        return footerLocationMap;
    }
}
