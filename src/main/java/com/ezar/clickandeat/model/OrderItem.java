package com.ezar.clickandeat.model;

public class OrderItem {

    private String menuItemId;
    
    private Integer menuItemNumber;
    
    private String menuItemTitle;

    private Double cost;

    private Integer quantity;

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getMenuItemNumber() {
        return menuItemNumber;
    }

    public void setMenuItemNumber(Integer menuItemNumber) {
        this.menuItemNumber = menuItemNumber;
    }

    public String getMenuItemTitle() {
        return menuItemTitle;
    }

    public void setMenuItemTitle(String menuItemTitle) {
        this.menuItemTitle = menuItemTitle;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
