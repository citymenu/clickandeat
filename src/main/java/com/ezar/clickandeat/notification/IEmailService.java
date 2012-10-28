package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;

 public interface IEmailService {

    void sendOrderNotificationToRestaurant(Order order) throws Exception;

    void sendOrderConfirmationToCustomer(Order order) throws Exception;

    void sendRestaurantAcceptedConfirmationToCustomer(Order order) throws Exception;

    void sendRestaurantDeclinedConfirmationToCustomer(Order order) throws Exception;

    void sendCustomerCancelledConfirmationToRestaurant(Order order) throws Exception;

    void sendCustomerCancelledConfirmationToCustomer(Order order) throws Exception;

    void sendSystemCancelledConfirmationToCustomer(Order order) throws Exception;

    void sendAutoCancelledConfirmationToCustomer(Order order) throws Exception;

    void sendAutoCancelledConfirmationToRestaurant(Order order) throws Exception;

    void sendDelistedConfirmationToRestaurant(Restaurant restaurant) throws Exception;

    void sendRelistedConfirmationToRestaurant(Restaurant restaurant) throws Exception;

    void sendOrderCancellationOfferToCustomer(Order order) throws Exception;

    void sendForOwnerApproval(Restaurant restaurant) throws Exception;

    void sendContentApproved(Restaurant restaurant) throws Exception;

    void sendContentRejected(Restaurant restaurant) throws Exception;

}
