package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {

    private String menuItemId;
    
    private Integer menuItemNumber;
    
    private String menuItemTitle;
    
    private String menuItemTypeName;

    private List<String> additionalItems;
    
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
        this.additionalItems = new ArrayList<String>();
    }

    public String getMenuItemTitle() {
        return menuItemTitle;
    }

    public void setMenuItemTitle(String menuItemTitle) {
        this.menuItemTitle = menuItemTitle;
    }

    public String getMenuItemTypeName() {
        return menuItemTypeName;
    }

    public void setMenuItemTypeName(String menuItemTypeName) {
        this.menuItemTypeName = menuItemTypeName;
    }

    public List<String> getAdditionalItems() {
        return additionalItems;
    }

    public void setAdditionalItems(List<String> additionalItems) {
        this.additionalItems = additionalItems;
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
