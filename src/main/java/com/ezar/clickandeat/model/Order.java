package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.CommissionUtils;
import com.ezar.clickandeat.util.DateTimeUtil;
import com.ezar.clickandeat.util.NumberUtil;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "orders")
public class Order extends PersistentObject {

    public static final String DELIVERY = "DELIVERY";
    public static final String COLLECTION = "COLLECTION";

    public static final String PAYMENT_ERROR = "ERROR";
    public static final String PAYMENT_PRE_AUTHORISED = "PREAUTHORISED";
    public static final String PAYMENT_CAPTURED = "CAPTURED";
    public static final String PAYMENT_REFUNDED = "REFUNDED";
    
    @Indexed(unique=true)
    private String orderId;

    private String userId;

    // Restaurant details
    private String restaurantId;
    private String restaurantName;
    private Boolean phoneOrdersOnly;
    private List<Discount> restaurantDiscounts;

    @Transient
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
    private String additionalInstructions;
    private boolean termsAndConditionsAccepted;
    private boolean restaurantWillDeliver;

    // Order timing details
    private DateTime orderCreatedTime;
    private DateTime orderPlacedTime;
    private DateTime expectedDeliveryTime;
    private DateTime expectedCollectionTime;
    private DateTime restaurantActionedTime; // Time the restaurant accepted or declined the order
    private DateTime restaurantConfirmedTime;
    private boolean deliveryTimeNonStandard = false;

    // Order cost details
    private Double orderItemCost;
    private Double deliveryCost;
    private Double extraSpendNeededForDelivery;
    private Double cardTransactionCost;
    private Double totalDiscount;
    private Double voucherDiscount;
    private Double totalCost;
    private Double restaurantCost; // Cost to restaurant (without any vouchers applied)
    private Double commission; // Commission to be retained by LlamaryComer

    // Order payment details
    private String transactionId;
    private String transactionStatus;
    private String authorisationCode;
    private String signature;
    private Double cardPaymentAmount;

    // Order tracking details
    private boolean canCheckout;
    private boolean canSubmitPayment;
    private boolean restaurantIsOpen;
    private String orderStatus;
    private String orderNotificationStatus;
    private String additionalRequestDetails;
    private DateTime lastCallPlacedTime;
    private Integer orderNotificationCallCount;
    private Boolean cancellationOfferEmailSent;
    private String restaurantDeclinedReason;
    private List<OrderUpdate> orderUpdates;
    private Boolean testOrder;

    // Indicates if the restaurant phone number was viewed
    private boolean phoneNumberViewed;

    // Indicates the order is deleted
    private boolean deleted;

    // Order amendments
    private List<OrderAmendment> orderAmendments;

    // Order voucher details
    private String voucherId;
    
    @Transient
    private Voucher voucher;

    public Order() {

        this.orderStatus = OrderWorkflowEngine.ORDER_STATUS_BASKET;
        this.orderNotificationStatus = OrderWorkflowEngine.NOTIFICATION_STATUS_NO_CALL_MADE;

        this.orderCreatedTime = new DateTime();
        
        this.customer = new Person();
        this.deliveryType = DELIVERY;
        this.deliveryAddress = new Address();
        this.billingAddress = new Address();

        this.cancellationOfferEmailSent = false;
        this.orderNotificationCallCount = 0;
        
        this.orderItems = new ArrayList<OrderItem>();
        this.orderDiscounts = new ArrayList<OrderDiscount>();
        this.restaurantDiscounts = new ArrayList<Discount>();

        this.orderUpdates = new ArrayList<OrderUpdate>();
        this.orderAmendments = new ArrayList<OrderAmendment>();

        this.testOrder = false;
    }


    /**
     * Updates order item costs
     */

    public void updateCosts() {

        // Check that any special offers are still valid (i.e. if the delivery date changes)
        List<OrderItem> toRemove = new ArrayList<OrderItem>();
        for( OrderItem orderItem: orderItems ) {
            String specialOfferId = orderItem.getMenuItemId();
            SpecialOffer specialOffer = restaurant.getSpecialOffer(specialOfferId);
            if( specialOffer != null ) {
                if( !specialOffer.isApplicableTo(this)) {
                    toRemove.add(orderItem);
                }
            }
        }
        orderItems.removeAll(toRemove);

        // Update order item costs
        double orderItemCost = 0d;
        for( OrderItem item: orderItems ) {
            orderItemCost += item.getCost() * item.getQuantity();
        }
        this.orderItemCost = orderItemCost;

        // Update all discount costs
        updateOrderDiscounts();

        this.totalDiscount = 0d;
        for( OrderDiscount discount: orderDiscounts ) {
            this.totalDiscount += discount.getDiscountAmount();
        }

        // Reset and update delivery costs
        this.deliveryCost = 0d;
        this.extraSpendNeededForDelivery = 0d;
        this.restaurantWillDeliver = true;

        if( DELIVERY.equals(this.getDeliveryType()) && this.orderItems.size() > 0 ) {
            DeliveryOptions deliveryOptions = this.restaurant.getDeliveryOptions();
            
            Double minimumOrderForDelivery = deliveryOptions.getMinimumOrderForDelivery(this.deliveryAddress);
            Double minimumOrderForFreeDelivery = deliveryOptions.getMinimumOrderForFreeDelivery();
            Double deliveryCharge = deliveryOptions.getDeliveryCharge(this.deliveryAddress);
            boolean allowFreeDelivery = deliveryOptions.isAllowFreeDelivery();
            boolean allowDeliveryBelowMinimumForFreeDelivery = deliveryOptions.isAllowDeliveryBelowMinimumForFreeDelivery();

            // Update whether or not the restaurant will deliver to this order
            this.restaurantWillDeliver = this.restaurant.willDeliverToLocation(this);

            if( !restaurantWillDeliver ) {
                this.deliveryCost = 0d;
                this.extraSpendNeededForDelivery = 0d;
            }
            else if( !allowFreeDelivery ) {
                if( deliveryCharge != null ) {
                    this.deliveryCost = deliveryCharge;
                }
                if(minimumOrderForDelivery != null && this.orderItemCost  < minimumOrderForDelivery ) {
                    this.extraSpendNeededForDelivery = minimumOrderForDelivery - this.orderItemCost;
                }
            }
            else {
                if(minimumOrderForDelivery != null && this.orderItemCost  < minimumOrderForDelivery ) {
                    this.extraSpendNeededForDelivery = minimumOrderForDelivery - this.orderItemCost;
                }
                if( minimumOrderForFreeDelivery != null && this.orderItemCost < minimumOrderForFreeDelivery ) {
                    if( allowDeliveryBelowMinimumForFreeDelivery && deliveryCharge != null ) {
                        this.deliveryCost = deliveryCharge;
                    }
                    else {
                        if( this.extraSpendNeededForDelivery == 0 ) {
                            this.extraSpendNeededForDelivery = minimumOrderForFreeDelivery - this.orderItemCost;
                        }
                    }
                }
            }
        }
        
        // Set the restaurant cost
        this.restaurantCost = this.orderItemCost + this.deliveryCost - this.totalDiscount;
        
        // Set the commission on this order
        this.commission = CommissionUtils.calculateCommission(this);
        
        // Apply any vouchers to the overall cost
        this.voucherDiscount = 0d;
        if( voucher != null ) {
            this.voucherDiscount = this.restaurantCost * voucher.getDiscount() / 100d;
        }           

        // Set the total cost
        this.totalCost = this.orderItemCost + this.deliveryCost - this.totalDiscount - this.voucherDiscount;
        
        // Update whether or not the restaurant is currently open
        updateRestaurantIsOpen();
        
        // Update whether or not the order is ready for checkout
        updateCanCheckout();
        
        // Update whether or not the order is ready for payment
        updateCanSubmitPayment();
    }


    /**
     * Updates all discounts applicable to this order
     */
    
    private void updateOrderDiscounts() {

        // Get all discounts applicable to this order
        Map<String,Discount> applicableDiscounts = new HashMap<String,Discount>();
        Discount nonCombinableDiscount = null; // Only include at most one discount which cannot be combined with others
        for( Discount discount: this.getRestaurant().getDiscounts()) {
            if( discount.isApplicableTo(this)) {
                if( discount.isCanCombineWithOtherDiscounts()) {
                    applicableDiscounts.put(discount.getDiscountId(), discount);
                }
                else {
                    if( nonCombinableDiscount == null ) {
                        nonCombinableDiscount = discount;                        
                    }
                    else if( discount.getMinimumOrderValue() > nonCombinableDiscount.getMinimumOrderValue()) {
                        nonCombinableDiscount = discount; // Keep the discount with the maximum minimum order value
                    }
                }
            }
        }
        if( nonCombinableDiscount != null ) {
            applicableDiscounts.put(nonCombinableDiscount.getDiscountId(),nonCombinableDiscount);
        }

        // Remove any existing discounts which are no longer applicable to this order
        List<OrderDiscount> discountsToRemove = new ArrayList<OrderDiscount>();
        for( OrderDiscount orderDiscount: orderDiscounts ) {
            if( !applicableDiscounts.containsKey(orderDiscount.getDiscountId())) {
                discountsToRemove.add(orderDiscount);
            }
        }
        if( discountsToRemove.size() > 0 ) {
            orderDiscounts.removeAll(discountsToRemove);
        }

        // Now either add or update the discounts
        for( Discount applicableDiscount: applicableDiscounts.values()) {
            OrderDiscount existingDiscount = getOrderDiscount(applicableDiscount.getDiscountId());
            if( existingDiscount == null ) {
                OrderDiscount newDiscount = applicableDiscount.createOrderDiscount(this);
                orderDiscounts.add(newDiscount);
            }
            else {
                applicableDiscount.updateOrderDiscount(this,existingDiscount);
            }
        }
        
    }
    
    
    /**
     * @param orderItem
     */
    
    public void addOrderItem(OrderItem orderItem) {
        OrderItem existingOrderItem = findExistingOrderItem(orderItem);
        if( existingOrderItem == null ) {
            orderItems.add(orderItem);
        }
        else {
            existingOrderItem.setQuantity(existingOrderItem.getQuantity() + orderItem.getQuantity());
        }
    }

        
    public void updateRestaurantIsOpen() {
        if( Order.DELIVERY.equals(deliveryType)) {
            restaurantIsOpen = !restaurant.getCollectionOnly() && restaurant.isOpen(expectedDeliveryTime == null ? new DateTime() : expectedDeliveryTime);
        }
        else {
            restaurantIsOpen = restaurant.isOpen(expectedCollectionTime == null ? new DateTime() : expectedCollectionTime);
        }
    }
    
    public void updateCanCheckout() {
        canCheckout = true;
        if( orderItems.size() == 0 ) {
            canCheckout = false;
        }
        if( extraSpendNeededForDelivery > 0 ) {
            canCheckout = false;
        }
        if( !restaurantIsOpen) {
            canCheckout = false;
        }
    }

    public void updateCanSubmitPayment() {
        
        canSubmitPayment = true;

        if(DELIVERY.equals(deliveryType)) {
            if( this.deliveryAddress == null || !this.deliveryAddress.isValid()) {
                canSubmitPayment = false;
            }
            if( !restaurantWillDeliver ) {
                canSubmitPayment = false;
            }
        }

        // If cannot checkout, cannot submit payment either
        if( !canCheckout ) {
            canSubmitPayment = false;
        }

    }


    /**
     * @param orderItemId
     */

    public void removeOrderItem(String orderItemId) {
        OrderItem orderItem = findByOrderItemId(orderItemId);
        if( orderItem != null ) {
            getOrderItems().remove(orderItem);
        }
    }


    /**
     * @param orderItemId
     * @param quantity
     */

    public void updateItemQuantity(String orderItemId, int quantity) {
        OrderItem orderItem = findByOrderItemId(orderItemId);
        if( orderItem != null ) {
            int newQuantity = orderItem.getQuantity() + quantity;
            if( newQuantity < 1 ) {
                getOrderItems().remove(orderItem);
            }
            else {
                orderItem.setQuantity(newQuantity);
            }
        }
    }
    
    
    /**
     * @param orderItem
     * @return
     */
    
    private OrderItem findExistingOrderItem(OrderItem orderItem) {
        for( OrderItem existingOrderItem: orderItems) {
            if( existingOrderItem.equals(orderItem)) {
                return existingOrderItem;
            }
        }
        return null;
    }


    /**
     * @param orderItemId
     * @return
     */

    private OrderItem findByOrderItemId(String orderItemId) {
        for( OrderItem orderItem: orderItems) {
            if( orderItemId.equals(orderItem.getOrderItemId())) {
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

        
    public boolean hasCashDiscount() {
        for( OrderDiscount discount: orderDiscounts ) {
            if( !Discount.DISCOUNT_FREE_ITEM.equals(discount.getDiscountType())) {
                return true;
            }
        }
        return false;
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

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Boolean getPhoneOrdersOnly() {
        return phoneOrdersOnly;
    }

    public void setPhoneOrdersOnly(Boolean phoneOrdersOnly) {
        this.phoneOrdersOnly = phoneOrdersOnly;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        this.restaurantId = restaurant.getRestaurantId();
        this.restaurantName = restaurant.getName();
        this.phoneOrdersOnly = restaurant.getPhoneOrdersOnly();
        this.restaurantDiscounts = restaurant.getDiscounts();
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

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void setAdditionalInstructions(String additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
    }

    public boolean getTermsAndConditionsAccepted() {
        return termsAndConditionsAccepted;
    }

    public void setTermsAndConditionsAccepted(boolean termsAndConditionsAccepted) {
        this.termsAndConditionsAccepted = termsAndConditionsAccepted;
    }

    public boolean getCanCheckout() {
        return canCheckout;
    }

    public void setCanCheckout(boolean canCheckout) {
        this.canCheckout = canCheckout;
    }

    public boolean getCanSubmitPayment() {
        return canSubmitPayment;
    }

    public void setCanSubmitPayment(boolean canSubmitPayment) {
        this.canSubmitPayment = canSubmitPayment;
    }

    public String getAuthorisationCode() {
        return authorisationCode;
    }

    public void setAuthorisationCode(String authorisationCode) {
        this.authorisationCode = authorisationCode;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Double getCardPaymentAmount() {
        return cardPaymentAmount;
    }

    public void setCardPaymentAmount(Double cardPaymentAmount) {
        this.cardPaymentAmount = cardPaymentAmount;
    }

    public boolean getRestaurantIsOpen() {
        return restaurantIsOpen;
    }

    public void setRestaurantIsOpen(boolean restaurantIsOpen) {
        this.restaurantIsOpen = restaurantIsOpen;
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

    public DateTime getOrderCreatedTime() {
        return orderCreatedTime;
    }

    public void setOrderCreatedTime(DateTime orderCreatedTime) {
        this.orderCreatedTime = orderCreatedTime;
    }

    public DateTime getLastCallPlacedTime() {
        return lastCallPlacedTime;
    }

    public void setLastCallPlacedTime(DateTime lastCallPlacedTime) {
        this.lastCallPlacedTime = lastCallPlacedTime;
    }

    public Integer getOrderNotificationCallCount() {
        return orderNotificationCallCount;
    }

    public void setOrderNotificationCallCount(Integer orderNotificationCallCount) {
        this.orderNotificationCallCount = orderNotificationCallCount;
    }

    public DateTime getOrderPlacedTime() {
        return orderPlacedTime;
    }

    public void setOrderPlacedTime(DateTime orderPlacedTime) {
        this.orderPlacedTime = orderPlacedTime;
    }

    public DateTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(DateTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }
    
    public String getExpectedDeliveryTimeString() {
        return DateTimeUtil.formatOrderDate(expectedDeliveryTime);
    }

    public String getExpectedCollectionTimeString() {
        return DateTimeUtil.formatOrderDate(expectedCollectionTime);
    }

    public boolean getDeliveryTimeNonStandard() {
        return deliveryTimeNonStandard;
    }

    public void setDeliveryTimeNonStandard(boolean deliveryTimeNonStandard) {
        this.deliveryTimeNonStandard = deliveryTimeNonStandard;
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

    public String getFormattedOrderItemCost() {
        return NumberUtil.format(orderItemCost == null? 0d: orderItemCost);
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

    public String getFormattedDeliveryCost() {
        return NumberUtil.format(deliveryCost == null? 0d: deliveryCost);
    }

    public Double getExtraSpendNeededForDelivery() {
        return extraSpendNeededForDelivery;
    }

    public String getFormattedExtraSpendNeededForDelivery() {
        return NumberUtil.format(extraSpendNeededForDelivery == null? 0d: extraSpendNeededForDelivery);
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

    public DateTime getRestaurantActionedTime() {
        return restaurantActionedTime;
    }

    public void setRestaurantActionedTime(DateTime restaurantActionedTime) {
        this.restaurantActionedTime = restaurantActionedTime;
    }

    public DateTime getRestaurantConfirmedTime() {
        return restaurantConfirmedTime;
    }

    public void setRestaurantConfirmedTime(DateTime restaurantConfirmedTime) {
        this.restaurantConfirmedTime = restaurantConfirmedTime;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public String getFormattedTotalCost() {
        return NumberUtil.format(totalCost == null? 0d: totalCost);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getRestaurantCost() {
        return restaurantCost;
    }

    public void setRestaurantCost(Double restaurantCost) {
        this.restaurantCost = restaurantCost;
    }

    public List<Discount> getRestaurantDiscounts() {
        return restaurantDiscounts;
    }

    public void setRestaurantDiscounts(List<Discount> restaurantDiscounts) {
        this.restaurantDiscounts = restaurantDiscounts;
    }

    public Double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(Double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Double getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(Double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public List<OrderUpdate> getOrderUpdates() {
        return orderUpdates;
    }

    public void setOrderUpdates(List<OrderUpdate> orderUpdates) {
        this.orderUpdates = orderUpdates;
    }

    public List<OrderAmendment> getOrderAmendments() {
        return orderAmendments;
    }

    public void setOrderAmendments(List<OrderAmendment> orderAmendments) {
        this.orderAmendments = orderAmendments;
    }

    public List<OrderDiscount> getOrderDiscounts() {
        return orderDiscounts;
    }

    public void setOrderDiscounts(List<OrderDiscount> orderDiscounts) {
        this.orderDiscounts = orderDiscounts;
    }
    
    public OrderDiscount getOrderDiscount(String discountId) {
        for( OrderDiscount orderDiscount: orderDiscounts ) {
            if( discountId.equals(orderDiscount.getDiscountId())) {
                return orderDiscount;
            }
        }
        return null;
    }

    public Boolean getTestOrder() {
        return testOrder;
    }

    public void setTestOrder(Boolean testOrder) {
        this.testOrder = testOrder;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        if( voucher != null ) {
            this.voucherId = voucher.getVoucherId();
            this.voucher = voucher;
        }
    }

    public boolean getRestaurantWillDeliver() {
        return restaurantWillDeliver;
    }

    public void setRestaurantWillDeliver(boolean restaurantWillDeliver) {
        this.restaurantWillDeliver = restaurantWillDeliver;
    }
    
    public boolean getPhoneNumberViewed() {
        return phoneNumberViewed;
    }

    public void setPhoneNumberViewed(boolean phoneNumberViewed) {
        this.phoneNumberViewed = phoneNumberViewed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
