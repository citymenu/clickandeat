package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class AreaDeliveryCharge {
    
    private List<String> areas;
    
    private Double deliveryCharge;
    
    public AreaDeliveryCharge() {
        this.areas = new ArrayList<String>();
    }

    public List<String> getAreas() {
        return areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
