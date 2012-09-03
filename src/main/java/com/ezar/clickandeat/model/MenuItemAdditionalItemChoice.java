package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.NumberUtil;

public class MenuItemAdditionalItemChoice {

    private String name;
    
    private Double cost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getFormattedCost() {
        return NumberUtil.format(cost);
    }

}
