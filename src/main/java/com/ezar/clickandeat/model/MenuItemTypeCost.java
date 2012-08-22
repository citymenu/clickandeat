package com.ezar.clickandeat.model;

public class MenuItemTypeCost {
    
    private String type;
    
    private Double cost;

    private Double additionalItemCost;

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

    public Double getAdditionalItemCost() {
        return additionalItemCost;
    }

    public void setAdditionalItemCost(Double additionalItemCost) {
        this.additionalItemCost = additionalItemCost;
    }
}
