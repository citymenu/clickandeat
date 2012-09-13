package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.NumberUtil;
import com.ezar.clickandeat.util.StringUtil;

public class MenuItemSubType {

    private String type;
    
    private Double cost;

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

}
