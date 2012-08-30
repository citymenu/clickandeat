package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.NumberUtil;

public class MenuItemSubType {

    private String type;
    
    private Double cost;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
