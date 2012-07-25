package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.templating.VelocityTemplatingService;
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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component(value="emailService")
public class EmailService implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class);

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private VelocityTemplatingService velocityTemplatingService;
    
    private String from;

    private String region;

    private String baseUrl;
    
    private Properties properties = new Properties();
    

    @Override
    public void afterPropertiesSet() throws Exception {
        String path = "/messages_" + region + ".properties";        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loading properties from file: " + path);
        }
        Resource resource = new ClassPathResource(path);
        properties.load(resource.getInputStream());
    }

    
    /**
     * @param order
     */
    
    public void sendOrderNotificationToRestaurant(Order order) {
    
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending order notification email to restaurant for order id: " + order.getOrderId());
        }

        String orderId = order.getOrderId();
        
        try {
            NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();
            String emailAddress = notificationOptions.getNotificationEmailAddress();
            String subjectFormat = properties.getProperty("restaurant-order-notification-subject");
            String subject = MessageFormat.format(subjectFormat,orderId);
            Map<String,Object> templateMap = new HashMap<String, Object>();
            templateMap.put("order",order);
            templateMap.put("url",baseUrl);
            String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.RESTAURANT_ORDER_NOTIFICATION_EMAIL_TEMPLATE);
            sendEmail(emailAddress, subject, emailContent);
            orderRepository.addOrderUpdate(orderId,"Sent email notification to restaurant");
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            orderRepository.addOrderUpdate(orderId,"Exception sending notification to restaurant: " + ex.getMessage());
        }
    }


    /**
     * @param order
     */
    
    public void sendOrderConfirmationToCustomer(Order order) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending order confirmation email to customer for order id: " + order.getOrderId());
        }

        String orderId = order.getOrderId();

        try {
            String emailAddress = order.getCustomer().getEmail();
            String subjectFormat = properties.getProperty("customer-order-confirmation-subject");
            String subject = MessageFormat.format(subjectFormat,orderId);
            Map<String,Object> templateMap = new HashMap<String, Object>();
            templateMap.put("order",order);
            String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.CUSTOMER_ORDER_CONFIRMATION_EMAIL_TEMPLATE);
            sendEmail(emailAddress, subject, emailContent);
            orderRepository.addOrderUpdate(orderId,"Sent email confirmation to customer");
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            orderRepository.addOrderUpdate(orderId,"Exception sending confirmation to customer: " + ex.getMessage());
        }
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
    @Value(value="${location.region}")
    public void setRegion(String region) {
        this.region = region;
    }

}
