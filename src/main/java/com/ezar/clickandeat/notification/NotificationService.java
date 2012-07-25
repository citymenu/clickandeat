package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.exception.ExceptionHandler;
import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
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
    private ExceptionHandler exceptionHandler;
    

    /**
     * @param order
     */

    public void sendOrderNotificationToRestaurant(Order order) throws Exception {

        String orderId = order.getOrderId();
        
        LOGGER.info("Sending order notification to restauarant for orderId [" + order.getOrderId() + "]");
        
        NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();

        // Send email notification to restaurant
        if( StringUtils.hasText(notificationOptions.getNotificationEmailAddress())) {
            emailService.sendOrderNotificationToRestaurant(order);
        }

        // Send SMS notification if setup
        if( notificationOptions.isReceiveSMSNotification()) {
            twilioService.sendOrderNotificationSMS(order);
        }

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

}
