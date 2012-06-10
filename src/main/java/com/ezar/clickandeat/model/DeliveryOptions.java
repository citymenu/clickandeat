package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOptions {

    private String deliveryOptionsSummary;

    private Double deliveryTimeMinutes;
    
    private Double minimumOrderForFreeDelivery;

    private Double deliveryCharge;

    private Double collectionDiscount;
    
    private Double minimumOrderForCollectionDiscount;

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

    public Double getMinimumOrderForFreeDelivery() {
        return minimumOrderForFreeDelivery;
    }

    public void setMinimumOrderForFreeDelivery(Double minimumOrderForFreeDelivery) {
        this.minimumOrderForFreeDelivery = minimumOrderForFreeDelivery;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Double getCollectionDiscount() {
        return collectionDiscount;
    }

    public void setCollectionDiscount(Double collectionDiscount) {
        this.collectionDiscount = collectionDiscount;
    }

    public Double getMinimumOrderForCollectionDiscount() {
        return minimumOrderForCollectionDiscount;
    }

    public void setMinimumOrderForCollectionDiscount(Double minimumOrderForCollectionDiscount) {
        this.minimumOrderForCollectionDiscount = minimumOrderForCollectionDiscount;
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
