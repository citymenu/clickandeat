package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.exception.ExceptionHandler;
import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component(value="notificationService")
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class);

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TwilioService twilioService;

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
            twilioService.sendOrderNotificationSMS(order);
            orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_NOTIFICATION_SMS_SENT);
        }
    }


    /**
     * @param order
     */
    
    public void placeOrderNotificationCallToRestaurant(Order order) throws Exception {

        LOGGER.info("Sending order notification to restauarant for orderId [" + order.getOrderId() + "]");

        NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();

        // Send notification call to restaurant
        if( notificationOptions.isReceiveNotificationCall()) {
            twilioService.makeOrderNotificationCall(order);
        }
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
    }


    /**
     * @param order
     * @throws Exception
     */

    public void sendAutoCancelledConfirmationToRestaurant(Order order) throws Exception {

        LOGGER.info("Sending customer cancelled confirmation to restaurant for orderId [" + order.getOrderId() + "]");

        NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();

        // Send email notification to restaurant
        if( StringUtils.hasText(notificationOptions.getNotificationEmailAddress())) {
            emailService.sendAutoCancelledConfirmationToRestaurant(order);
        }

    }


    /**
     * @param order
     * @throws Exception
     */

    public void sendDelistedConfirmationToRestaurant(Order order) throws Exception {

        LOGGER.info("Sending customer cancelled confirmation to restaurant for orderId [" + order.getOrderId() + "]");

        NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();

        // Send email notification to restaurant
        if( StringUtils.hasText(notificationOptions.getNotificationEmailAddress())) {
            emailService.sendDelistedConfirmationToRestaurant(order);
        }

    }


    
}
