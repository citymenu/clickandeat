package com.ezar.clickandeat.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    private static final DecimalFormat formatter;

    static {
        formatter = new DecimalFormat();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
    }

    private int number;
    
    private String itemId;
    
    private String title;
    
    private String subtitle;
    
    private String description;
    
    private String iconClass;

    private Double cost;

    private List<MenuItemTypeCost> menuItemTypeCosts;

    private List<String> additionalItemChoices;
    
    private Double additionalItemCost;
    
    private Integer additionalItemChoiceLimit;
            

    public MenuItem() {
        this.menuItemTypeCosts = new ArrayList<MenuItemTypeCost>();
        this.additionalItemChoices = new ArrayList<String>();
    }

    public int getNumber() {
        return number;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getFormattedCost() {
        return cost == null? "": formatter.format(cost);
    }
    
    public List<MenuItemTypeCost> getMenuItemTypeCosts() {
        return menuItemTypeCosts;
    }

    public void setMenuItemTypeCosts(List<MenuItemTypeCost> menuItemTypeCosts) {
        this.menuItemTypeCosts = menuItemTypeCosts;
    }

    public List<String> getAdditionalItemChoices() {
        return additionalItemChoices;
    }

    public void setAdditionalItemChoices(List<String> additionalItemChoices) {
        this.additionalItemChoices = additionalItemChoices;
    }

    public Double getAdditionalItemCost() {
        return additionalItemCost;
    }

    public void setAdditionalItemCost(Double additionalItemCost) {
        this.additionalItemCost = additionalItemCost;
    }

    public Integer getAdditionalItemChoiceLimit() {
        return additionalItemChoiceLimit;
    }

    public void setAdditionalItemChoiceLimit(Integer additionalItemChoiceLimit) {
        this.additionalItemChoiceLimit = additionalItemChoiceLimit;
    }
}
