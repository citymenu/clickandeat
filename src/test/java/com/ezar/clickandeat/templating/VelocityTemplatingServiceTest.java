package com.ezar.clickandeat.templating;

import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.notification.TwilioServiceImpl;
import com.ezar.clickandeat.repository.OrderRepository;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class VelocityTemplatingServiceTest implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(VelocityTemplatingServiceTest.class);
    
    @Autowired
    private VelocityTemplatingService velocityTemplatingService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private TwilioServiceImpl twilioService;

    private String timeZone;
    
    private String locale;

    private Locale systemLocale;


    @Override
    public void afterPropertiesSet() throws Exception {
        String[] localeArray = locale.split("_");
        this.systemLocale = new Locale(localeArray[0],localeArray[1]);
    }

    @Test
    public void testBuildOrderNotificationCallResponse() throws Exception {
    
        Order order = orderRepository.create();
        order.setDeliveryType(Order.DELIVERY);
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String url = twilioService.buildTwilioUrl(TwilioServiceImpl.FULL_ORDER_CALL_URL, order.getOrderId());
        templateModel.put("url", StringEscapeUtils.escapeHtml(url));
        templateModel.put("delivery",order.getDeliveryType().toLowerCase());
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.NOTIFICATION_CALL_TEMPLATE);
        Assert.assertNotNull(xml);
        LOGGER.info("Generated xml [" + xml + "]");
    }


    @Test
    public void testFullOrderCallResponse() throws Exception {

        Order order = orderRepository.create();
        order.setDeliveryType(Order.DELIVERY);
        order.setExpectedDeliveryTime(new DateTime(DateTimeZone.forID(timeZone)));
        
        // Add a delivery address to the order
        Address deliveryAddress = new Address();
        deliveryAddress.setAddress1("80 Peel Road");
        deliveryAddress.setTown("South Woodford");
        deliveryAddress.setRegion("London");
        deliveryAddress.setPostCode("E18 2LG");
        order.setDeliveryAddress(deliveryAddress);
        
        // Add two items to the order
        OrderItem item1 = new OrderItem();
        item1.setMenuItemId("ITEM1");
        item1.setMenuItemNumber(101);
        item1.setQuantity(2);
        order.addOrderItem(item1);
        
        OrderItem item2 = new OrderItem();
        item2.setMenuItemId("ITEM2");
        item2.setMenuItemNumber(0);
        item2.setQuantity(1);
        item2.setMenuItemTitle("Spinach Pakora");
        order.addOrderItem(item2);

        Map<String,Object> templateModel = new HashMap<String, Object>();
        String url = twilioService.buildTwilioUrl(TwilioServiceImpl.FULL_ORDER_CALL_PROCESS_URL, order.getOrderId());
        templateModel.put("url", StringEscapeUtils.escapeHtml(url));
        templateModel.put("order",order);
        templateModel.put("today",new LocalDate(DateTimeZone.forID(timeZone)));
        templateModel.put("locale",systemLocale);
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.FULL_ORDER_CALL_TEMPLATE);
        Assert.assertNotNull(xml);
        LOGGER.info("Generated xml:\n" + xml );
    }

    @Test
    public void testBuildRestaurantOrderNotificationEmail() throws Exception {

        Order order = orderRepository.create();
        order.setDeliveryType(Order.DELIVERY);

        // Add two items to the order
        OrderItem item1 = new OrderItem();
        item1.setMenuItemId("ITEM1");
        item1.setMenuItemNumber(101);
        item1.setMenuItemTitle("Spinach Pakora");
        item1.setQuantity(2);
        order.addOrderItem(item1);

        OrderItem item2 = new OrderItem();
        item2.setMenuItemId("ITEM2");
        item2.setMenuItemNumber(0);
        item2.setQuantity(1);
        item2.setMenuItemTitle("Onion Bhajii###s");
        order.addOrderItem(item2);

        // Build a restaurant for the order
        Restaurant restaurant = new Restaurant();
        NotificationOptions notificationOptions = new NotificationOptions();
        notificationOptions.setNotificationEmailAddress("mishimaltd@gmail.com");
        restaurant.setNotificationOptions(notificationOptions);
        
        order.setRestaurant(restaurant);
        
        order.setOrderItemCost(1.355d);
        order.setTotalCost(35.403);
        
        Map<String,Object> templateModel = new HashMap<String, Object>();
        templateModel.put("order",order);
        String text = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.RESTAURANT_ORDER_NOTIFICATION_EMAIL_TEMPLATE);
        Assert.assertNotNull(text);
        LOGGER.info("Generated text:\n" + text );
    }


    
    
    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    

    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale;
    }

}
