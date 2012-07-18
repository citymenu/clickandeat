package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.email.EmailService;
import com.ezar.clickandeat.exception.ExceptionHandler;
import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.OrderUpdate;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component(value="notificationService")
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class);

    @Autowired
    private OrderRepository orderRepository;

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
            orderRepository.addOrderUpdate(orderId,"Sent email notification to restaurant");
        }

        // Send SMS notification if setup
        if( notificationOptions.isReceiveSMSNotification()) {
            twilioService.sendOrderNotificationSMS(order);
            orderRepository.addOrderUpdate(orderId,"Sent SMS notification to restaurant");
        }

        // Either send full order details over telephone or just send notification
        if( notificationOptions.isTakeOrderOverTelephone()) {
            twilioService.makeFullOrderCall(order);
            orderRepository.addOrderUpdate(orderId,"Made full order call to restauarant");
        }
        else if( notificationOptions.isReceiveNotificationCall()) {
            twilioService.makeOrderNotificationCall(order);
            orderRepository.addOrderUpdate(orderId,"Made order notification call to restauarant");
        }
    }

}
