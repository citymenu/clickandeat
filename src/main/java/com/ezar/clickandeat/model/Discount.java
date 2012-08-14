package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class Discount {
    
    public static final String DISCOUNT_CASH = "DISCOUNT_CASH";
    public static final String DISCOUNT_PERCENTAGE = "DISCOUNT_PERCENTAGE";
    public static final String DISCOUNT_FREE_ITEM = "DISCOUNT_FREE_ITEM";
        
    private String discountId;
    
    private String title;
    
    private String description;

    private String discountType;
    
    private boolean collection;
    
    private boolean delivery;
    
    private Double minimumOrderValue;
    
    private Double discountAmount;
    
    private List<String> freeItems;
    
    private List<DiscountApplicableTime> discountApplicableTimes;


    public Discount() {
        this.freeItems = new ArrayList<String>();
        this.discountApplicableTimes = new ArrayList<DiscountApplicableTime>();
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public Double getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(Double minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public List<String> getFreeItems() {
        return freeItems;
    }

    public void setFreeItems(List<String> freeItems) {
        this.freeItems = freeItems;
    }

    public List<DiscountApplicableTime> getDiscountApplicableTimes() {
        return discountApplicableTimes;
    }

    public void setDiscountApplicableTimes(List<DiscountApplicableTime> discountApplicableTimes) {
        this.discountApplicableTimes = discountApplicableTimes;
    }
}
