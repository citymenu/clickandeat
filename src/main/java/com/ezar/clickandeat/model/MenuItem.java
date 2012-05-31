package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    private int number;
    
    private String title;
    
    private String subtitle;
    
    private String description;
    
    private String iconClass;

    private Double cost;

    private List<MenuItemTypeCost> menuItemTypeCosts;

    public MenuItem() {
        this.menuItemTypeCosts = new ArrayList<MenuItemTypeCost>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public List<MenuItemTypeCost> getMenuItemTypeCosts() {
        return menuItemTypeCosts;
    }

    public void setMenuItemTypeCosts(List<MenuItemTypeCost> menuItemTypeCosts) {
        this.menuItemTypeCosts = menuItemTypeCosts;
    }
}
