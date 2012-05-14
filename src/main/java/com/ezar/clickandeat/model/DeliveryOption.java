package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOption {
    
    private Double deliveryCharge;

    private Double minimumOrderPrice;
    
    private Double deliveryRadius;

    private List<String> areasDeliveredTo = new ArrayList<String>();

    @Override
    public String toString() {
        return "DeliveryOption{" +
                "deliveryCharge=" + deliveryCharge +
                ", minimumOrderPrice=" + minimumOrderPrice +
                ", deliveryRadius=" + deliveryRadius +
                ", areasDeliveredTo=" + areasDeliveredTo +
                '}';
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Double getMinimumOrderPrice() {
        return minimumOrderPrice;
    }

    public void setMinimumOrderPrice(Double minimumOrderPrice) {
        this.minimumOrderPrice = minimumOrderPrice;
    }

    public Double getDeliveryRadius() {
        return deliveryRadius;
    }

    public void setDeliveryRadius(Double deliveryRadius) {
        this.deliveryRadius = deliveryRadius;
    }

    public List<String> getAreasDeliveredTo() {
        return areasDeliveredTo;
    }

    public void setAreasDeliveredTo(List<String> areasDeliveredTo) {
        this.areasDeliveredTo = areasDeliveredTo;
    }
}
