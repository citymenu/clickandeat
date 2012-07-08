package com.ezar.clickandeat.model;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
public class Order extends PersistentObject {

    public static final String STATUS_BASKET = "BASKET";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_COMPLETE = "COMPLETE";
    
    public static final String DELIVERY = "DELIVERY";
    public static final String COLLECTION = "COLLECTION";

    
    @Indexed(unique=true)
    private String orderId;

    private String userId;

    private String restaurantId;

    @DBRef
    private Restaurant restaurant;

    private Person customer;

    private String deliveryType;

    private String paymentType;
    
    private Address deliveryAddress;

    private Address billingAddress;
    
    private String orderStatus;

    private DateTime orderPlacedTime;

    private DateTime requestedTime;

    private DateTime expectedTime;

    private Double orderItemCost;

    private Double deliveryCost;
    
    private Double cardTransactionCost;

    private Double totalDiscount;

    private Double totalCost;

    private List<OrderItem> orderItems;
    
    private List<OrderDiscount> orderDiscounts;

    private List<String> orderUpdates;

    public Order() {
        this.orderItems = new ArrayList<OrderItem>();
        this.orderUpdates = new ArrayList<String>();
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

        // Set the total cost
        this.totalCost = this.orderItemCost - this.totalDiscount;
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
    
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public DateTime getOrderPlacedTime() {
        return orderPlacedTime;
    }

    public void setOrderPlacedTime(DateTime orderPlacedTime) {
        this.orderPlacedTime = orderPlacedTime;
    }

    public DateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(DateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public DateTime getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(DateTime expectedTime) {
        this.expectedTime = expectedTime;
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

    public Double getCardTransactionCost() {
        return cardTransactionCost;
    }

    public void setCardTransactionCost(Double cardTransactionCost) {
        this.cardTransactionCost = cardTransactionCost;
    }

    public Double getTotalCost() {
        return totalCost;
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

    public List<String> getOrderUpdates() {
        return orderUpdates;
    }

    public void setOrderUpdates(List<String> orderUpdates) {
        this.orderUpdates = orderUpdates;
    }

    public List<OrderDiscount> getOrderDiscounts() {
        return orderDiscounts;
    }

    public void setOrderDiscounts(List<OrderDiscount> orderDiscounts) {
        this.orderDiscounts = orderDiscounts;
    }
}
