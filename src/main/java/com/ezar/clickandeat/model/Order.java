package com.ezar.clickandeat.model;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
public class Order extends BaseObject {
    
    private String orderId;

    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    @DBRef
    private User user;

    @DBRef
    private Restaurant restaurant;

    private Person customer;

    private String deliveryType;

    private String paymentType;
    
    private Address deliveryAddress;
    
    private String orderStatus;

    private DateTime orderPlacedTime;

    private DateTime requestedTime;

    private DateTime expectedTime;

    private Double orderItemCost;
    
    private Double deliveryCost;
    
    private Double cardTransactionCost;
    
    private Double totalCost;
    
    private List<String> orderUpdates = new ArrayList<String>();


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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<String> getOrderUpdates() {
        return orderUpdates;
    }

    public void setOrderUpdates(List<String> orderUpdates) {
        this.orderUpdates = orderUpdates;
    }
}
