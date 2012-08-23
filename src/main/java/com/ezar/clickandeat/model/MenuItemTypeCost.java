package com.ezar.clickandeat.model;

import java.text.DecimalFormat;

public class MenuItemTypeCost {

    private static final DecimalFormat formatter;

    private String type;
    
    private Double cost;

    private Double additionalItemCost;

    static {
        formatter = new DecimalFormat();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
    }


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
        return cost == null? "": formatter.format(cost);
    }

    public Double getAdditionalItemCost() {
        return additionalItemCost;
    }

    public void setAdditionalItemCost(Double additionalItemCost) {
        this.additionalItemCost = additionalItemCost;
    }

    public Double getNullSafeAdditionalItemCost() {
        return additionalItemCost == null? 0d: additionalItemCost;
    }

}
