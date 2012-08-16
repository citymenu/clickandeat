package com.ezar.clickandeat.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

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

    private boolean canCombineWithOtherDiscounts;
    
    private Double minimumOrderValue;
    
    private Double discountAmount;
    
    private List<String> freeItems;
    
    private List<DiscountApplicableTime> discountApplicableTimes;


    public Discount() {
        this.freeItems = new ArrayList<String>();
        this.discountApplicableTimes = new ArrayList<DiscountApplicableTime>();
    }


    /**
     * @param order
     * @return
     */

    public boolean isApplicableTo(Order order) {

        if( minimumOrderValue != null && order.getOrderItemCost() < minimumOrderValue ) {
            return false;
        }

        if( Order.DELIVERY.equals(order.getDeliveryType())) {
            if( !delivery ) {
                return false;
            }
            DateTime expectedDeliveryTime = order.getExpectedDeliveryTime() == null? new DateTime(): order.getExpectedDeliveryTime();
            DiscountApplicableTime applicableTime = getDiscountApplicableTime(expectedDeliveryTime);
            if( applicableTime == null || !applicableTime.getApplicable()) {
                return false;
            }
            if( applicableTime.getApplicableFrom() == null || applicableTime.getApplicableTo() == null ) {
                return true;
            }
            LocalTime time = expectedDeliveryTime.toLocalTime();
            return !time.isBefore(applicableTime.getApplicableFrom()) && !time.isAfter(applicableTime.getApplicableTo());
        }
        else {
            if( !collection ) {
                return false;
            }
            DateTime expectedCollectionTime = order.getExpectedCollectionTime() == null? new DateTime(): order.getExpectedCollectionTime();
            DiscountApplicableTime applicableTime = getDiscountApplicableTime(expectedCollectionTime);
            if( applicableTime == null || !applicableTime.getApplicable()) {
                return false;
            }
            if( applicableTime.getApplicableFrom() == null || applicableTime.getApplicableTo() == null ) {
                return true;
            }
            LocalTime time = expectedCollectionTime.toLocalTime();
            return !time.isBefore(applicableTime.getApplicableFrom()) && !time.isAfter(applicableTime.getApplicableTo());
        }
    }


    /**
     * @param order
     * @return
     */
    
    public OrderDiscount createOrderDiscount(Order order) {
        OrderDiscount orderDiscount = new OrderDiscount();
        orderDiscount.setDiscountId(discountId);
        orderDiscount.setTitle(title);
        orderDiscount.setDiscountAmount(calculateDiscountAmount(order));
        return orderDiscount;
    }


    /**
     * @param order
     * @param orderDiscount
     * @return
     */

    public void updateOrderDiscount(Order order, OrderDiscount orderDiscount) {
        orderDiscount.setDiscountAmount(calculateDiscountAmount(order));        
    }


    /**
     * @param order
     * @return
     */
    
    private Double calculateDiscountAmount(Order order) {
        if(DISCOUNT_FREE_ITEM.equals(discountType)) {
            return 0d;
        }
        else if(DISCOUNT_CASH.equals(discountType)) {
            return discountAmount;
        }
        else {
            return order.getOrderItemCost() * discountAmount / 100;
        }
    }
    
    /**
     * @param dateTime
     * @return
     */

    private DiscountApplicableTime getDiscountApplicableTime(DateTime dateTime ) {
        int dayOfWeek = dateTime.getDayOfWeek();
        for( DiscountApplicableTime time: discountApplicableTimes ) {
            if( dayOfWeek == time.getDayOfWeek()) {
                return time;
            }
        }
        return null;
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

    public boolean isCanCombineWithOtherDiscounts() {
        return canCombineWithOtherDiscounts;
    }

    public void setCanCombineWithOtherDiscounts(boolean canCombineWithOtherDiscounts) {
        this.canCombineWithOtherDiscounts = canCombineWithOtherDiscounts;
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
