package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class TwilioServiceTest {

    private static final Logger LOGGER = Logger.getLogger(TwilioServiceTest.class);
    
    @Autowired
    private TwilioService twilioService;

    @Test
    @Ignore
    public void testSendSms() throws Exception {
        Restaurant restaurant = new Restaurant();
        NotificationOptions options = new NotificationOptions();
        options.setNotificationSMSNumber("+447881626584");
        restaurant.setNotificationOptions(options);
        Order order = new Order();
        order.setDeliveryType(Order.DELIVERY);
        order.setRestaurant(restaurant);
        twilioService.sendOrderNotificationSMS(order);
    }
    
    
}
