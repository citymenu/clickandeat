package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.templating.VelocityTemplatingService;
import com.ezar.clickandeat.util.SecurityUtils;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component(value="emailService")
public class EmailServiceImpl implements IEmailService, InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private VelocityTemplatingService velocityTemplatingService;

    @Autowired
    private SecurityUtils securityUtils;

    private String from;

    private String locale;

    private String baseUrl;
    
    private Properties properties = new Properties();
    

    @Override
    public void afterPropertiesSet() throws Exception {
        String path = "/messages_" + locale.split("_")[0] + ".properties";
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loading properties from file: " + path);
        }
        Resource resource = new ClassPathResource(path);
        properties.load(resource.getInputStream());
    }

    
    /**
     * @param order
     */
    
    @Override
    public void sendOrderNotificationToRestaurant(Order order) throws Exception {
    
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending order notification email to restaurant for order id: " + order.getOrderId());
        }

        NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();
        String emailAddress = notificationOptions.getNotificationEmailAddress();
        String subjectFormat = properties.getProperty("restaurant-order-notification-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        templateMap.put("baseUrl",baseUrl);
        String acceptCurl = securityUtils.encrypt("orderId=" + order.getOrderId() + "#action=" + OrderWorkflowEngine.ACTION_RESTAURANT_ACCEPTS);
        String declineCurl = securityUtils.encrypt("orderId=" + order.getOrderId() + "#action=" + OrderWorkflowEngine.ACTION_RESTAURANT_DECLINES);
        templateMap.put("acceptCurl", acceptCurl);
        templateMap.put("declineCurl", declineCurl);

        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.RESTAURANT_ORDER_NOTIFICATION_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param order
     */
    
    @Override
    public void sendOrderConfirmationToCustomer(Order order) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending order confirmation email to customer for order id: " + order.getOrderId());
        }

        String emailAddress = order.getCustomer().getEmail();
        String subjectFormat = properties.getProperty("customer-order-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.CUSTOMER_ORDER_CONFIRMATION_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param order
     */

    @Override
    public void sendRestaurantAcceptedConfirmationToCustomer(Order order) throws Exception {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending restaurant accepted confirmation email to customer for order id: " + order.getOrderId());
        }

        String emailAddress = order.getCustomer().getEmail();
        String subjectFormat = properties.getProperty("restaurant-order-accepted-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId(), StringEscapeUtils.unescapeHtml(order.getRestaurant().getName()));
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.RESTAURANT_ACCEPTED_ORDER_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param order
     */

    @Override
    public void sendRestaurantDeclinedConfirmationToCustomer(Order order) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending restaurant declined confirmation email to customer for order id: " + order.getOrderId());
        }

        String emailAddress = order.getCustomer().getEmail();
        String subjectFormat = properties.getProperty("restaurant-order-declined-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId(),StringEscapeUtils.unescapeHtml(order.getRestaurant().getName()));
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.RESTAURANT_DECLINED_ORDER_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param order
     */

    @Override
    public void sendCustomerCancelledConfirmationToRestaurant(Order order) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending customer cancelled confirmation email to restaurant for order id: " + order.getOrderId());
        }

        String emailAddress = order.getRestaurant().getNotificationOptions().getNotificationEmailAddress();
        String subjectFormat = properties.getProperty("customer-order-cancelled-restaurant-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId(),StringEscapeUtils.unescapeHtml(order.getRestaurant().getName()));
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.CUSTOMER_CANCELLED_ORDER_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param order
     */

    @Override
    public void sendCustomerCancelledConfirmationToCustomer(Order order) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending customer cancelled confirmation email to customer for order id: " + order.getOrderId());
        }

        String emailAddress = order.getCustomer().getEmail();
        String subjectFormat = properties.getProperty("customer-order-cancelled-customer-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.CUSTOMER_CANCELLED_ORDER_CONFIRMATION_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param order
     */

    @Override
    public void sendRestaurantCancelledConfirmationToCustomer(Order order) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending restaurant cancelled confirmation email to customer for order id: " + order.getOrderId());
        }

        String emailAddress = order.getCustomer().getEmail();
        String subjectFormat = properties.getProperty("restaurant-order-cancelled-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.RESTAURANT_CANCELLED_ORDER_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param order
     */

    @Override
    public void sendAutoCancelledConfirmationToCustomer(Order order) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending auto cancelled confirmation email to customer for order id: " + order.getOrderId());
        }

        String emailAddress = order.getCustomer().getEmail();
        String subjectFormat = properties.getProperty("customer-auto-cancelled-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.AUTO_CANCELLED_CUSTOMER_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param order
     */

    @Override
    public void sendAutoCancelledConfirmationToRestaurant(Order order) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending auto cancelled confirmation email to restaurant for order id: " + order.getOrderId());
        }

        String emailAddress = order.getRestaurant().getNotificationOptions().getNotificationEmailAddress();
        String subjectFormat = properties.getProperty("restaurant-auto-cancelled-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.AUTO_CANCELLED_RESTAURANT_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    /**
     * @param restaurant
     */

    @Override
    public void sendDelistedConfirmationToRestaurant(Restaurant restaurant) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending delisted email to restaurant id: " + restaurant.getRestaurantId());
        }

        String emailAddress = restaurant.getNotificationOptions().getNotificationEmailAddress();
        String subjectFormat = properties.getProperty("restaurant-delisted-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,StringEscapeUtils.unescapeHtml(restaurant.getName()));
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("restaurant",restaurant);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.RESTAURANT_DELISTED_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }

    
    /**
     * @param restaurant
     */

    @Override
    public void sendRelistedConfirmationToRestaurant(Restaurant restaurant) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending relisted email to restaurant id: " + restaurant.getRestaurantId());
        }

        String emailAddress = restaurant.getNotificationOptions().getNotificationEmailAddress();
        String subjectFormat = properties.getProperty("restaurant-relisted-confirmation-subject");
        String subject = MessageFormat.format(subjectFormat,StringEscapeUtils.unescapeHtml(restaurant.getName()));
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("restaurant",restaurant);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.RESTAURANT_RELISTED_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }

    
    /**
     * @param order
     */

    @Override
    public void sendOrderCancellationOfferToCustomer(Order order) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending cancelled offer email to customer for order id: " + order.getOrderId());
        }

        String emailAddress = order.getCustomer().getEmail();
        String subjectFormat = properties.getProperty("customer-cancellation-offer-subject");
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.CUSTOMER_CANCELLATION_OFFER_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);

    }


    /**
     * @param to
     * @param subject
     * @param text
     */
    
    private void sendEmail(final String to, final String subject, final String text ) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(to);
                message.setFrom(from);
                message.setSubject(subject);
                message.setText(text,true);
            }
        };
        mailSender.send(preparator);
        
    }


    @Required
    @Value(value="${baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    

    @Required
    @Value(value="${email.from}")
    public void setFrom(String from) {
        this.from = from;
    }

    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale;
    }

}
