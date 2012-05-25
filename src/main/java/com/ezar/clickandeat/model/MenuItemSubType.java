package com.ezar.clickandeat.model;

import java.util.List;

public class MenuItemSubType {
    
    private String name;
    
    private String description;
    
    private Double price;

    private List<MenuItemOption> options;

    private boolean allowMultipleOptions;

    private Double optionPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<MenuItemOption> getOptions() {
        return options;
    }

    public void setOptions(List<MenuItemOption> options) {
        this.options = options;
    }

    public boolean isAllowMultipleOptions() {
        return allowMultipleOptions;
    }

    public void setAllowMultipleOptions(boolean allowMultipleOptions) {
        this.allowMultipleOptions = allowMultipleOptions;
    }

    public Double getOptionPrice() {
        return optionPrice;
    }

    public void setOptionPrice(Double optionPrice) {
        this.optionPrice = optionPrice;
    }
}
