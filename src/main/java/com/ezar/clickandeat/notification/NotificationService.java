package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.exception.ExceptionHandler;
import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.util.PhoneNumberUtils;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component(value="notificationService")
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class);

    @Autowired
    private IEmailService emailService;
    
    @Autowired
    private ITwilioService twilioService;

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;

    @Autowired
    private ExceptionHandler exceptionHandler;
    

    /**
     * @param order
     */

    public void sendOrderNotificationToRestaurant(Order order) throws Exception {

        LOGGER.info("Sending order notification to restauarant for orderId [" + order.getOrderId() + "]");
        
        NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();

        // Send email notification to restaurant
        if( StringUtils.hasText(notificationOptions.getNotificationEmailAddress())) {
            emailService.sendOrderNotificationToRestaurant(order);
        }

        // Send SMS notification if setup
        if( notificationOptions.isReceiveSMSNotification()) {
            try {
                twilioService.sendOrderNotificationSMS(order);
                orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_SEND_SMS);
            }
            catch( Exception ex) {
                LOGGER.error("Exception sending SMS",ex);
            }
        }
    }


    /**
     * @param order
     */
    
    public void placeOrderNotificationCallToRestaurant(Order order) throws Exception {

        LOGGER.info("Placing order notification call to restauarant for orderId [" + order.getOrderId() + "]");

        twilioService.makeOrderNotificationCall(order);
    }


    /**
     * @param order
     */

    public void sendOrderConfirmationToCustomer(Order order) throws Exception {

        LOGGER.info("Sending order confirmation to customer for orderId [" + order.getOrderId() + "]");

        // Send email notification to customer
        if( StringUtils.hasText(order.getCustomer().getEmail())) {
            emailService.sendOrderConfirmationToCustomer(order);
        }

    }


    /**
     * @param order
     */
    
    public void sendRestaurantAcceptedConfirmationToCustomer(Order order) throws Exception {

        LOGGER.info("Sending restaurant accepted confirmation to customer for orderId [" + order.getOrderId() + "]");

        // Send email notification to customer
        if( StringUtils.hasText(order.getCustomer().getEmail())) {
            emailService.sendRestaurantAcceptedConfirmationToCustomer(order);
        }
    }


    /**
     * @param order
     */

    public void sendRestaurantDeclinedConfirmationToCustomer(Order order) throws Exception {

        LOGGER.info("Sending restaurant declined confirmation to customer for orderId [" + order.getOrderId() + "]");

        // Send email notification to customer
        if( StringUtils.hasText(order.getCustomer().getEmail())) {
            emailService.sendRestaurantDeclinedConfirmationToCustomer(order);
        }

        // If the user has specified a mobile number, send SMS
        if( PhoneNumberUtils.isMobileNumber(order.getCustomer().getTelephone())) {
            try {
                twilioService.sendRestaurantDeclinedNotificationSMS(order);
            }
            catch(Exception ex) {
                LOGGER.error("Exception sending SMS", ex);
            }
        }

    }


    /**
     * @param order
     * @throws Exception
     */
    
    public void sendCustomerCancelledConfirmationToRestaurant(Order order) throws Exception {

        LOGGER.info("Sending customer cancelled confirmation to restaurant for orderId [" + order.getOrderId() + "]");

        NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();

        // Send email notification to restaurant
        if( StringUtils.hasText(notificationOptions.getNotificationEmailAddress())) {
            emailService.sendCustomerCancelledConfirmationToRestaurant(order);
        }
    }


    /**
     * @param order
     * @throws Exception
     */

    public void sendCustomerCancelledConfirmationToCustomer(Order order) throws Exception {

        LOGGER.info("Sending customer cancelled confirmation to customer for orderId [" + order.getOrderId() + "]");

        // Send email notification to customer
        if( StringUtils.hasText(order.getCustomer().getEmail())) {
            emailService.sendCustomerCancelledConfirmationToCustomer(order);
        }
    }


    /**
     * @param order
     * @throws Exception
     */

    public void sendRestaurantCancelledConfirmationToCustomer(Order order) throws Exception {

        LOGGER.info("Sending restaurant cancelled confirmation to customer for orderId [" + order.getOrderId() + "]");

        // Send email notification to customer
        if( StringUtils.hasText(order.getCustomer().getEmail())) {
            emailService.sendRestaurantCancelledConfirmationToCustomer(order);
        }
    }


    /**
     * @param order
     * @throws Exception
     */

    public void sendAutoCancelledConfirmationToCustomer(Order order) throws Exception {

        LOGGER.info("Sending auto cancelled confirmation to customer for orderId [" + order.getOrderId() + "]");

        // Send email notification to customer
        if( StringUtils.hasText(order.getCustomer().getEmail())) {
            emailService.sendAutoCancelledConfirmationToCustomer(order);
        }

        // If the user has specified a mobile number, send SMS
        if(PhoneNumberUtils.isMobileNumber(order.getCustomer().getTelephone())) {
            try {
                twilioService.sendAutoCancelledNotificationSMS(order);
            }
            catch(Exception ex) {
                LOGGER.error("Exception sending SMS",ex);
            }
        }

    }


    /**
     * @param order
     * @throws Exception
     */

    public void sendAutoCancelledConfirmationToRestaurant(Order order) throws Exception {

        LOGGER.info("Sending auto cancelled confirmation to restaurant for orderId [" + order.getOrderId() + "]");

        NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();

        // Send email notification to restaurant
        if( StringUtils.hasText(notificationOptions.getNotificationEmailAddress())) {
            emailService.sendAutoCancelledConfirmationToRestaurant(order);
        }

    }


    /**
     * @param restaurant
     * @throws Exception
     */

    public void sendDelistedConfirmationToRestaurant(Restaurant restaurant) throws Exception {

        LOGGER.info("Sending delisted notification to restaurant id [" + restaurant.getRestaurantId() + "]");

        // Send email notification to restaurant
        if( StringUtils.hasText(restaurant.getNotificationOptions().getNotificationEmailAddress())) {
            emailService.sendDelistedConfirmationToRestaurant(restaurant);
        }
    }


    /**
     * @param restaurant
     * @throws Exception
     */

    public void sendRelistedConfirmationToRestaurant(Restaurant restaurant) throws Exception {

        LOGGER.info("Sending relisted notification to restaurant id [" + restaurant.getRestaurantId() + "]");

        // Send email notification to restaurant
        if( StringUtils.hasText(restaurant.getNotificationOptions().getNotificationEmailAddress())) {
            emailService.sendRelistedConfirmationToRestaurant(restaurant);
        }
    }


    /**
     * @param order
     * @throws Exception
     */

    public void sendOrderCancellationOfferToCustomer(Order order) throws Exception {

        LOGGER.info("Sending offer to cancel order to customer for orderId [" + order.getOrderId() + "]");

        // Send email notification to customer
        if( StringUtils.hasText(order.getCustomer().getEmail())) {
            emailService.sendOrderCancellationOfferToCustomer(order);
        }
    }


}
