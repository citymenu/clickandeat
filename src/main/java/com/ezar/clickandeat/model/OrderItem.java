package com.ezar.clickandeat.model;

public class OrderItem {
    
    private MenuItem menuItem;

    private Double cost;
    
    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
