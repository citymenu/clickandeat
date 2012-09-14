package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOptions {

    private String deliveryOptionsSummary;

    private Double deliveryTimeMinutes;
    
    private Double minimumOrderForFreeDelivery;

    private Boolean allowDeliveryOrdersBelowMinimum;
    
    private Double deliveryCharge;

    private Double deliveryRadiusInKilometres;

    private List<String> areasDeliveredTo;

    public DeliveryOptions() {
        this.areasDeliveredTo = new ArrayList<String>();
    }

    public String getDeliveryOptionsSummary() {
        return deliveryOptionsSummary;
    }

    public void setDeliveryOptionsSummary(String deliveryOptionsSummary) {
        this.deliveryOptionsSummary = deliveryOptionsSummary;
    }

    public Double getDeliveryTimeMinutes() {
        return deliveryTimeMinutes;
    }

    public void setDeliveryTimeMinutes(Double deliveryTimeMinutes) {
        this.deliveryTimeMinutes = deliveryTimeMinutes;
    }

    public String getFormattedDeliveryTimeMinutes() {
        return NumberUtil.formatStrict(deliveryTimeMinutes);
    }
    
    public Double getMinimumOrderForFreeDelivery() {
        return minimumOrderForFreeDelivery;
    }

    public void setMinimumOrderForFreeDelivery(Double minimumOrderForFreeDelivery) {
        this.minimumOrderForFreeDelivery = minimumOrderForFreeDelivery;
    }

    public Boolean getAllowDeliveryOrdersBelowMinimum() {
        return allowDeliveryOrdersBelowMinimum;
    }

    public void setAllowDeliveryOrdersBelowMinimum(Boolean allowDeliveryOrdersBelowMinimum) {
        this.allowDeliveryOrdersBelowMinimum = allowDeliveryOrdersBelowMinimum;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Double getDeliveryRadiusInKilometres() {
        return deliveryRadiusInKilometres;
    }

    public void setDeliveryRadiusInKilometres(Double deliveryRadiusInKilometres) {
        this.deliveryRadiusInKilometres = deliveryRadiusInKilometres;
    }

    public List<String> getAreasDeliveredTo() {
        return areasDeliveredTo;
    }

    public void setAreasDeliveredTo(List<String> areasDeliveredTo) {
        this.areasDeliveredTo = areasDeliveredTo;
    }
}
