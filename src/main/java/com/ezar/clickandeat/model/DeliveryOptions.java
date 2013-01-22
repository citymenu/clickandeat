package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOptions {

    private static final int DEFAULT_DELIVERY_TIME = 45;
    private static final int DEFAULT_COLLECTION_TIME = 20;
    
    private String deliveryOptionsSummary;

    private int deliveryTimeMinutes;
    private int collectionTimeMinutes;

    // Does the restaurant do collection orders only
    private boolean collectionOnly;

    // Costings around delivery
    private Double minimumOrderForDelivery;
    private Double deliveryCharge;
    private boolean allowFreeDelivery;
    private Double minimumOrderForFreeDelivery;
    private boolean allowDeliveryBelowMinimumForFreeDelivery;

    private Double deliveryRadiusInKilometres;
    private List<String> areasDeliveredTo;
    
    private List<AreaDeliveryCharge> areaDeliveryCharges;

    public DeliveryOptions() {
        this.deliveryTimeMinutes = DEFAULT_DELIVERY_TIME;
        this.collectionTimeMinutes = DEFAULT_COLLECTION_TIME;
        this.areasDeliveredTo = new ArrayList<String>();
        this.areaDeliveryCharges = new ArrayList<AreaDeliveryCharge>();
    }

    public String getDeliveryOptionsSummary() {
        return deliveryOptionsSummary;
    }

    public void setDeliveryOptionsSummary(String deliveryOptionsSummary) {
        this.deliveryOptionsSummary = deliveryOptionsSummary;
    }

    public int getDeliveryTimeMinutes() {
        return deliveryTimeMinutes;
    }

    public void setDeliveryTimeMinutes(int deliveryTimeMinutes) {
        this.deliveryTimeMinutes = deliveryTimeMinutes;
    }

    public int getCollectionTimeMinutes() {
        return collectionTimeMinutes;
    }

    public void setCollectionTimeMinutes(int collectionTimeMinutes) {
        this.collectionTimeMinutes = collectionTimeMinutes;
    }

    public Double getMinimumOrderForFreeDelivery() {
        return minimumOrderForFreeDelivery;
    }

    public void setMinimumOrderForFreeDelivery(Double minimumOrderForFreeDelivery) {
        this.minimumOrderForFreeDelivery = minimumOrderForFreeDelivery;
    }

    public Double getMinimumOrderForDelivery() {
        return minimumOrderForDelivery;
    }

    public void setMinimumOrderForDelivery(Double minimumOrderForDelivery) {
        this.minimumOrderForDelivery = minimumOrderForDelivery;
    }

    public boolean isAllowFreeDelivery() {
        return allowFreeDelivery;
    }

    public void setAllowFreeDelivery(boolean allowFreeDelivery) {
        this.allowFreeDelivery = allowFreeDelivery;
    }

    public boolean isAllowDeliveryBelowMinimumForFreeDelivery() {
        return allowDeliveryBelowMinimumForFreeDelivery;
    }

    public void setAllowDeliveryBelowMinimumForFreeDelivery(boolean allowDeliveryBelowMinimumForFreeDelivery) {
        this.allowDeliveryBelowMinimumForFreeDelivery = allowDeliveryBelowMinimumForFreeDelivery;
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

    public boolean isCollectionOnly() {
        return collectionOnly;
    }

    public void setCollectionOnly(boolean collectionOnly) {
        this.collectionOnly = collectionOnly;
    }

    public List<AreaDeliveryCharge> getAreaDeliveryCharges() {
        return areaDeliveryCharges;
    }

    public void setAreaDeliveryCharges(List<AreaDeliveryCharge> areaDeliveryCharges) {
        this.areaDeliveryCharges = areaDeliveryCharges;
    }
}
