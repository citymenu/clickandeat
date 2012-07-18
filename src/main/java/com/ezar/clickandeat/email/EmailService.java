package com.ezar.clickandeat.email;

import com.ezar.clickandeat.model.Order;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component(value="emailService")
public class EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class);

    /**
     * @param order
     * @throws Exception
     */
    
    public void sendOrderNotificationToRestaurant(Order order) throws Exception {
        LOGGER.info("Sending order notification to restaurant for orderId [" + order.getOrderId() + "]");
    }
}
