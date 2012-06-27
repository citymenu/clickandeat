package com.ezar.clickandeat.email;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component(value="emailService")
public class EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class);

    /**
     * @param order
     * @param restaurant
     * @throws Exception
     */
    
    public void sendOrderNotificationToRestaurant(Order order,Restaurant restaurant) throws Exception {
        LOGGER.info("Sending order notification for orderId [" + order.getOrderId() + "] to restaurantId [" + restaurant.getRestaurantId() + "]");
    }
}
