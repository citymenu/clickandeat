package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.NumberUtil;

import java.util.List;

public class OrderDiscount {
    
    private String discountId;

    private String discountType;
    
    private String title;

    private Double discountAmount;

    private List<String> freeItems;

    private String freeItem;

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getSelectedFreeItem() {
        return freeItem;
    }

    public void setSelectedFreeItem(String freeItem) {
        this.freeItem = freeItem;
    }
    
    public String getFormattedAmount() {
        return NumberUtil.format(discountAmount == null? 0d: discountAmount);
    }
}
