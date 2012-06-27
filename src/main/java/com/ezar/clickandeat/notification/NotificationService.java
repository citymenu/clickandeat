package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.email.EmailService;
import com.ezar.clickandeat.exception.ExceptionHandler;
import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TwilioService twilioService;

    @Autowired
    private ExceptionHandler exceptionHandler;

    /**
     * @param orderId
     */

    public void sendOrderNotification(String orderId) {
        
        LOGGER.info("Sending order notification for orderId [" + orderId + "]");

        try {

            Order order = orderRepository.findByOrderId(orderId);
            if( order == null ) {
                throw new RuntimeException("Could not find order with orderId " + orderId);
            }

            String restaurantId = order.getRestaurantId();
            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
            if( restaurant == null ) {
                throw new RuntimeException("Could not find restaurant with restaurantId " + restaurantId);
            }

            NotificationOptions notificationOptions = restaurant.getNotificationOptions();
            
            // Send email notification to restaurant
            if( StringUtils.hasText(notificationOptions.getNotificationEmailAddress())) {
                emailService.sendOrderNotificationToRestaurant(order,restaurant);
                orderRepository.addOrderUpdate(orderId,"Sent email notification to restaurant");
            }
            
            // Send SMS notification if setup
            if( notificationOptions.isReceiveSMSNotification()) {
                twilioService.sendOrderNotificationSMS(order,restaurant);
                orderRepository.addOrderUpdate(orderId,"Sent SMS notification to restaurant");
            }

            // Either send full order details over telephone or just send notification
            if( notificationOptions.isTakeOrderOverTelephone()) {
                twilioService.makeFullOrderCall(order,restaurant);
                orderRepository.addOrderUpdate(orderId,"Made full order call to restauarant");
            }
            else if( notificationOptions.isReceiveNotificationCall()) {
                twilioService.makeOrderNotificationCall(order,restaurant);
                orderRepository.addOrderUpdate(orderId,"Made order notification call to restauarant");
            }

        }
        catch( Exception ex ) {
            LOGGER.error("Error sending order notification",ex);
            orderRepository.addOrderUpdate(orderId, "Error sending notification: " + ex.getMessage());
            orderRepository.updateOrderStatus(orderId, Order.STATUS_ERROR);
            exceptionHandler.handleException(ex);
        }

    }
}
