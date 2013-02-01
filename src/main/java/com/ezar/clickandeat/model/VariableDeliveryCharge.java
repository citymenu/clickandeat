package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class VariableDeliveryCharge implements Comparable<VariableDeliveryCharge> {

    private Double minimumOrderValue;

    private Double deliveryCharge;

    public VariableDeliveryCharge() {
    }
    
    public int compareTo(VariableDeliveryCharge charge ) {
        return minimumOrderValue < charge.getMinimumOrderValue()? -1: 1;
    }

    public Double getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(Double minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
