package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.NumberUtil;
import com.ezar.clickandeat.util.StringUtil;

import java.text.DecimalFormat;

public class MenuItemTypeCost {

    private String type;
    
    private Double cost;

    private Double additionalItemCost;

    public String getEscapedType() {
        return StringUtil.escape(type);
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
        return NumberUtil.format(cost);
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
