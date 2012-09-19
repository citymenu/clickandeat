package com.ezar.clickandeat.util;

import com.ezar.clickandeat.config.MessageFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.SortedSet;
import java.util.TreeSet;

@Component(value="cuisineProvider")
public class CuisineProvider {
    
    private final SortedSet<String> cuisineList;

    public CuisineProvider() {
        this.cuisineList = new TreeSet<String>(StringUtils.commaDelimitedListToSet(MessageFactory.getMessage("restaurants.cuisines",false)));
    }
    
    public SortedSet<String> getCuisineList() {
        return cuisineList;
    }
}
