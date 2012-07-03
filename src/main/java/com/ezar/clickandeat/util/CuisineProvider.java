package com.ezar.clickandeat.util;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Component(value="cuisineProvider")
public class CuisineProvider {
    
    private SortedSet<String> cuisineList;

    public SortedSet<String> getCuisineList() {
        return cuisineList;
    }
    
    @Required
    @Value(value="${restaurants.cuisines}")
    public void setCuisineList(String cuisineList) {
        this.cuisineList = new TreeSet<String>(StringUtils.commaDelimitedListToSet(cuisineList));
    }
}
