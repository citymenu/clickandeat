package com.ezar.clickandeat.model;

import org.joda.time.DateTime;

public class OrderAmendment {
    
    private String description;
    
    private DateTime created;
    
    private Double previousTotalCost;

    private Double previousRestaurantCost;
    
    private Double totalCost;
    
    private Double restaurantCost;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public Double getPreviousTotalCost() {
        return previousTotalCost;
    }

    public void setPreviousTotalCost(Double previousTotalCost) {
        this.previousTotalCost = previousTotalCost;
    }

    public Double getPreviousRestaurantCost() {
        return previousRestaurantCost;
    }

    public void setPreviousRestaurantCost(Double previousRestaurantCost) {
        this.previousRestaurantCost = previousRestaurantCost;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getRestaurantCost() {
        return restaurantCost;
    }

    public void setRestaurantCost(Double restaurantCost) {
        this.restaurantCost = restaurantCost;
    }
}
