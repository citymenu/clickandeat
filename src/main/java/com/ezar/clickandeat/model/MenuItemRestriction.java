package com.ezar.clickandeat.model;

import org.joda.time.LocalTime;

import java.util.List;

public class MenuItemRestriction {

    private List<Integer> availableDays;
    
    private LocalTime availableFrom;
    
    private LocalTime availableTo;

    public List<Integer> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<Integer> availableDays) {
        this.availableDays = availableDays;
    }

    public LocalTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalTime getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalTime availableTo) {
        this.availableTo = availableTo;
    }
}
