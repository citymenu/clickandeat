package com.ezar.clickandeat.model;

import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
public class Order extends PersistentObject {

    public static final String DELIVERY = "DELIVERY";
    public static final String COLLECTION = "COLLECTION";

    @Indexed(unique=true)
    private String orderId;

    private String uuid;
    private String userId;
    private String restaurantId;

    @DBRef
    private Restaurant restaurant;

    // Order components
    private List<OrderItem> orderItems;
    private List<OrderDiscount> orderDiscounts;

    // Customer/delivery/billing details 
    private Person customer;
    private String deliveryType;
    private String paymentType;
    private Address deliveryAddress;
    private Address billingAddress;

    // Order timing details
    private DateTime orderPlacedTime;
    private DateTime requestedDeliveryTime;
    private DateTime expectedDeliveryTime;
    private DateTime requestedCollectionTime;
    private DateTime expectedCollectionTime;

    // Order cost details
    private Double orderItemCost;
    private Double deliveryCost;
    private Double extraSpendNeededForDelivery;
    private Double cardTransactionCost;
    private Double collectionDiscount;
    private Double totalDiscount;
    private Double totalCost;

    // Order tracking details
    private String orderStatus;
    private String orderNotificationStatus;
    private String additionalRequestDetails;
    private DateTime lastCallPlacedTime;
    private int orderNotificationCallCount;
    private Boolean cancellationOfferEmailSent;
    private String cardTransactionId;
    private String cardTransactionStatus;
    private String restaurantDeclinedReason;
    private List<OrderUpdate> orderUpdates;

    
    public Order() {
        this.orderStatus = OrderWorkflowEngine.ORDER_STATUS_BASKET;
        this.orderNotificationStatus = OrderWorkflowEngine.NOTIFICATION_STATUS_NO_CALL_MADE;
        this.cancellationOfferEmailSent = false;
        
        this.orderItems = new ArrayList<OrderItem>();
        this.orderUpdates = new ArrayList<OrderUpdate>();
        this.orderDiscounts = new ArrayList<OrderDiscount>();
    }


    /**
     * Updates order item costs
     */

    public void updateCosts() {

        // Update order item costs
        double orderItemCost = 0d;
        for( OrderItem item: orderItems ) {
            orderItemCost += item.getCost() * item.getQuantity();
        }
        this.orderItemCost = orderItemCost;

        // Update all discount costs
        double totalDiscount = 0d;
        for( OrderDiscount discount: orderDiscounts ) {
            totalDiscount += discount.getDiscount();
        }
        this.totalDiscount = totalDiscount;

        // Reset and update delivery cost and collection discount
        this.deliveryCost = 0d;
        this.extraSpendNeededForDelivery = 0d;
        this.collectionDiscount = 0d;

        if( DELIVERY.equals(this.getDeliveryType()) && this.orderItems.size() > 0 ) {

            Double minimumOrderForFreeDelivery = this.restaurant.getDeliveryOptions().getMinimumOrderForFreeDelivery();
            Double deliveryCharge = this.restaurant.getDeliveryOptions().getDeliveryCharge();
            Boolean allowDeliveryOrdersBelowMinimum = this.restaurant.getDeliveryOptions().getAllowDeliveryOrdersBelowMinimum();

            if(minimumOrderForFreeDelivery != null && this.orderItemCost < minimumOrderForFreeDelivery ) {
                if(allowDeliveryOrdersBelowMinimum != null && allowDeliveryOrdersBelowMinimum ) {
                    this.deliveryCost = deliveryCharge;
                }
                else {
                    this.extraSpendNeededForDelivery = minimumOrderForFreeDelivery - this.orderItemCost;
                }
            }
        }
        else if( COLLECTION.equals(this.getDeliveryType()) && this.orderItems.size() > 0 ) {
            Double discountForCollection = this.restaurant.getDeliveryOptions().getCollectionDiscount();
            Double minimumOrderForCollectionDiscount = this.restaurant.getDeliveryOptions().getMinimumOrderForCollectionDiscount();

            if( discountForCollection != null && minimumOrderForCollectionDiscount != null ) {
                if( this.orderItemCost >= minimumOrderForCollectionDiscount ) {
                    this.collectionDiscount = ( this.orderItemCost * discountForCollection ) / 100d;
                }
            }
        }
        
        // Set the total cost
        this.totalCost = this.orderItemCost + this.deliveryCost - this.collectionDiscount - this.totalDiscount;
    }
    
    
    /**
     * @param orderItem
     */
    
    public void addOrderItem(OrderItem orderItem) {
        OrderItem existingOrderItem = findByMenuItemId(orderItem.getMenuItemId());
        if( existingOrderItem == null ) {
            orderItems.add(orderItem);
        }
        else {
            existingOrderItem.setQuantity(existingOrderItem.getQuantity() + orderItem.getQuantity());
        }
    }


    /**
     * 
     * @param itemId
     * @param quantity
     */
    public void removeOrderItem(String itemId, Integer quantity ) {
        OrderItem orderItem = findByMenuItemId(itemId);
        if( orderItem != null ) {
            int newQuantity = orderItem.getQuantity() - quantity;
            if( newQuantity < 1 ) {
                getOrderItems().remove(orderItem);
            }
            else {
                orderItem.setQuantity(newQuantity);
            }
        }
    }
    
    
    /**
     * @param menuItemId
     * @return
     */
    
    private OrderItem findByMenuItemId(String menuItemId) {
        for( OrderItem orderItem: orderItems) {
            if( menuItemId.equals(orderItem.getMenuItemId())) {
                return orderItem;
            }
        }
        return null;
    }

    
    /**
     * @param dateTime
     * @return
     */

    public boolean isForCurrentDate(DateTime dateTime) {
        LocalDate today = new LocalDate(dateTime.getZone());
        LocalDate day = dateTime.toLocalDate();
        return today.equals(day);
    }


    /**
     * @param text
     */

    public void addOrderUpdate(String text) {
        OrderUpdate orderUpdate = new OrderUpdate();
        orderUpdate.setText(text);
        orderUpdate.setUpdateTime(new DateTime());
        orderUpdates.add(orderUpdate);
    }

    
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderNotificationStatus() {
        return orderNotificationStatus;
    }

    public void setOrderNotificationStatus(String orderNotificationStatus) {
        this.orderNotificationStatus = orderNotificationStatus;
    }

    public DateTime getLastCallPlacedTime() {
        return lastCallPlacedTime;
    }

    public void setLastCallPlacedTime(DateTime lastCallPlacedTime) {
        this.lastCallPlacedTime = lastCallPlacedTime;
    }

    public int getOrderNotificationCallCount() {
        return orderNotificationCallCount;
    }

    public void setOrderNotificationCallCount(int orderNotificationCallCount) {
        this.orderNotificationCallCount = orderNotificationCallCount;
    }

    public DateTime getOrderPlacedTime() {
        return orderPlacedTime;
    }

    public void setOrderPlacedTime(DateTime orderPlacedTime) {
        this.orderPlacedTime = orderPlacedTime;
    }

    public DateTime getRequestedDeliveryTime() {
        return requestedDeliveryTime;
    }

    public void setRequestedDeliveryTime(DateTime requestedDeliveryTime) {
        this.requestedDeliveryTime = requestedDeliveryTime;
    }

    public DateTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(DateTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public DateTime getRequestedCollectionTime() {
        return requestedCollectionTime;
    }

    public void setRequestedCollectionTime(DateTime requestedCollectionTime) {
        this.requestedCollectionTime = requestedCollectionTime;
    }

    public DateTime getExpectedCollectionTime() {
        return expectedCollectionTime;
    }

    public void setExpectedCollectionTime(DateTime expectedCollectionTime) {
        this.expectedCollectionTime = expectedCollectionTime;
    }

    public Double getOrderItemCost() {
        return orderItemCost;
    }

    public void setOrderItemCost(Double orderItemCost) {
        this.orderItemCost = orderItemCost;
    }

    public Double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public Double getExtraSpendNeededForDelivery() {
        return extraSpendNeededForDelivery;
    }

    public void setExtraSpendNeededForDelivery(Double extraSpendNeededForDelivery) {
        this.extraSpendNeededForDelivery = extraSpendNeededForDelivery;
    }

    public Double getCardTransactionCost() {
        return cardTransactionCost;
    }

    public void setCardTransactionCost(Double cardTransactionCost) {
        this.cardTransactionCost = cardTransactionCost;
    }

    public Double getCollectionDiscount() {
        return collectionDiscount;
    }

    public String getAdditionalRequestDetails() {
        return additionalRequestDetails;
    }

    public void setAdditionalRequestDetails(String additionalRequestDetails) {
        this.additionalRequestDetails = additionalRequestDetails;
    }

    public Boolean getCancellationOfferEmailSent() {
        return cancellationOfferEmailSent;
    }

    public void setCancellationOfferEmailSent(Boolean cancellationOfferEmailSent) {
        this.cancellationOfferEmailSent = cancellationOfferEmailSent;
    }

    public String getRestaurantDeclinedReason() {
        return restaurantDeclinedReason;
    }

    public void setRestaurantDeclinedReason(String restaurantDeclinedReason) {
        this.restaurantDeclinedReason = restaurantDeclinedReason;
    }

    public void setCollectionDiscount(Double collectionDiscount) {
        this.collectionDiscount = collectionDiscount;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public String getCardTransactionId() {
        return cardTransactionId;
    }

    public void setCardTransactionId(String cardTransactionId) {
        this.cardTransactionId = cardTransactionId;
    }

    public String getCardTransactionStatus() {
        return cardTransactionStatus;
    }

    public void setCardTransactionStatus(String cardTransactionStatus) {
        this.cardTransactionStatus = cardTransactionStatus;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(Double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public List<OrderUpdate> getOrderUpdates() {
        return orderUpdates;
    }

    public void setOrderUpdates(List<OrderUpdate> orderUpdates) {
        this.orderUpdates = orderUpdates;
    }

    public List<OrderDiscount> getOrderDiscounts() {
        return orderDiscounts;
    }

    public void setOrderDiscounts(List<OrderDiscount> orderDiscounts) {
        this.orderDiscounts = orderDiscounts;
    }
}
