package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.Voucher;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.VoucherRepository;
import com.ezar.clickandeat.templating.VelocityTemplatingService;
import com.ezar.clickandeat.util.SecurityUtils;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Component(value="emailService")
public class EmailServiceImpl implements IEmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VoucherRepository voucherRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private VelocityTemplatingService velocityTemplatingService;

    @Autowired
    private GeoLocationService geoLocationService;
    
    @Autowired
    private SecurityUtils securityUtils;

    private String from;

    private String adminEmailAddress;

    private int cancelCutoffMinutes;

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
        String subjectFormat = MessageFactory.getMessage("email-subject.restaurant-order-notification-subject", false);
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        if( Order.DELIVERY.equals(order.getDeliveryType())) {
            double[] restaurantLocation = order.getRestaurant().getAddress().getLocation();
            double[] customerLocation = order.getDeliveryAddress().getLocation();
            if( restaurantLocation != null && customerLocation != null ) {
                Double distance = geoLocationService.getDistance(restaurantLocation, customerLocation);
                templateMap.put("distance",distance);
            }
        }
        String acceptCurl = securityUtils.encrypt("orderId=" + order.getOrderId() + "#action=" + OrderWorkflowEngine.ACTION_RESTAURANT_ACCEPTS);
        String declineCurl = securityUtils.encrypt("orderId=" + order.getOrderId() + "#action=" + OrderWorkflowEngine.ACTION_RESTAURANT_DECLINES);
        String acceptWithDeliveryCurl = securityUtils.encrypt("orderId=" + order.getOrderId() + "#action=" + OrderWorkflowEngine.ACTION_RESTAURANT_ACCEPTS_WITH_DELIVERY_DETAIL);
        templateMap.put("acceptCurl", acceptCurl);
        templateMap.put("declineCurl", declineCurl);
        templateMap.put("acceptWithDeliveryCurl", acceptWithDeliveryCurl);

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
        String subjectFormat = MessageFactory.getMessage("email-subject.customer-order-confirmation-subject", false);
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
        String subjectFormat = MessageFactory.getMessage("email-subject.restaurant-order-accepted-confirmation-subject",false);
        String subject = MessageFormat.format(subjectFormat,order.getOrderId(), StringEscapeUtils.unescapeHtml(order.getRestaurant().getName()));
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        if( order.getDeliveryTimeNonStandard()) {
            
            // Give the customer an opportunity to cancel the order
            templateMap.put("allowCancel", true);
            templateMap.put("cancelCutoffTime", new DateTime().plusMinutes(cancelCutoffMinutes));
            String cancelCurl = securityUtils.encrypt("orderId=" + order.getOrderId() + "#action=" + OrderWorkflowEngine.ACTION_CUSTOMER_CANCELS);
            templateMap.put("cancelCurl", cancelCurl);

        }
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
        String subjectFormat = MessageFactory.getMessage("email-subject.restaurant-order-declined-confirmation-subject",false);
        String subject = MessageFormat.format(subjectFormat,order.getOrderId(),StringEscapeUtils.unescapeHtml(order.getRestaurant().getName()));

        // Create a voucher to try to keep this customer
        Voucher voucher = voucherRepository.createVoucher(5d);
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        templateMap.put("voucherId",voucher.getVoucherId());
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
        String subjectFormat = MessageFactory.getMessage("email-subject.restaurant-order-cancelled-confirmation-subject",false);
        String subject = MessageFormat.format(subjectFormat,order.getOrderId(),StringEscapeUtils.unescapeHtml(order.getRestaurant().getName()));
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.CUSTOMER_CANCELLED_ORDER_CONFIRMATION_EMAIL_TEMPLATE);
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
        String subjectFormat = MessageFactory.getMessage("email-subject.customer-order-cancelled-customer-confirmation-subject",false);
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.CUSTOMER_CANCELLED_ORDER_EMAIL_TEMPLATE);
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
        String subjectFormat = MessageFactory.getMessage("email-subject.restaurant-order-cancelled-confirmation-subject",false);
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());

        // Create a voucher to try to keep this customer for the next order
        Voucher voucher = voucherRepository.createVoucher(5d);
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        templateMap.put("voucherId",voucher.getVoucherId());
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
        String subjectFormat = MessageFactory.getMessage("email-subject.customer-auto-cancelled-confirmation-subject",false);
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        
        // Create a voucher to try to keep this customer for the next order
        Voucher voucher = voucherRepository.createVoucher(Voucher.DEFAULT_VOUCHER_DISCOUNT);
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        templateMap.put("voucherId",voucher.getVoucherId());
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
        String subjectFormat = MessageFactory.getMessage("email-subject.restaurant-auto-cancelled-confirmation-subject",false);
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
        String subjectFormat = MessageFactory.getMessage("email-subject.restaurant-delisted-confirmation-subject",false);
        String subject = MessageFormat.format(subjectFormat,StringEscapeUtils.unescapeHtml(restaurant.getName()));
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("restaurant",restaurant);
        String relistCurl = securityUtils.encrypt("restaurantId=" + restaurant.getRestaurantId());
        templateMap.put("relistCurl", relistCurl);
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
        String subjectFormat = MessageFactory.getMessage("email-subject.restaurant-relisted-confirmation-subject",false);
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
        String subjectFormat = MessageFactory.getMessage("email-subject.customer-cancellation-offer-subject",false);
        String subject = MessageFormat.format(subjectFormat,order.getOrderId());
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("order",order);
        String cancelCurl = securityUtils.encrypt("orderId=" + order.getOrderId() + "#action=" + OrderWorkflowEngine.ACTION_CUSTOMER_CANCELS);
        templateMap.put("cancelCurl", cancelCurl);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.CUSTOMER_CANCELLATION_OFFER_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }


    @Override
    public void sendForOwnerApproval(Restaurant restaurant) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending email to restaurant for owner approval id: " + restaurant.getRestaurantId());
        }

        String emailAddress = restaurant.getNotificationOptions().getNotificationEmailAddress();
        String subject = MessageFactory.getMessage("email-subject.owner-content-approval-subject",false);
        Map<String,Object> templateMap = new HashMap<String, Object>();
        //templateMap.put("restaurant",restaurant);
        String restCurl = "restaurantId=" + restaurant.getRestaurantId();
        templateMap.put("restCurl", restCurl);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.OWNER_CONTENT_APPROVAL_EMAIL_TEMPLATE);
        sendEmail(emailAddress, subject, emailContent);
    }

    @Override
    public void sendContentApproved(Restaurant restaurant) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending email to confirm that owner has approved content. id: " + restaurant.getRestaurantId());
        }

        String subject = MessageFactory.getMessage("email-subject.owner-content-approved-subject",false);
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("restaurant",restaurant);
        String restCurl = "restaurantId=" + restaurant.getRestaurantId();
        templateMap.put("restCurl", restCurl);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.OWNER_CONTENT_APPROVED_EMAIL_TEMPLATE);
        sendEmail(adminEmailAddress , subject, emailContent);
    }

    @Override
    public void sendContentRejected(Restaurant restaurant) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending email to notify that the restaurant content has been rejected by the owner. id: " + restaurant.getRestaurantId());
        }

        String subject = MessageFactory.getMessage("email-subject.owner-content-rejected-subject",false);
        Map<String,Object> templateMap = new HashMap<String, Object>();
        templateMap.put("restaurant",restaurant);
        String restCurl = "restaurantId=" + restaurant.getRestaurantId();
        templateMap.put("restCurl", restCurl);
        String emailContent = velocityTemplatingService.mergeContentIntoTemplate(templateMap, VelocityTemplatingService.OWNER_CONTENT_REJECTED_EMAIL_TEMPLATE);
        sendEmail(adminEmailAddress, subject, emailContent);
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
                message.getMimeMessage().setSubject(subject, "utf-8");
                message.setText(text,true);
            }
        };
        mailSender.send(preparator);
        
    }


    @Required
    @Value(value="${email.from}")
    public void setFrom(String from) {
        this.from = from;
    }

    @Required
    @Value(value="${email.adminaddress}")
    public void setAdminEmailAddress(String adminEmailAddress) {
        this.adminEmailAddress = adminEmailAddress;
    }

    @Required
    @Value(value="${order.cancelCutoffMinutes}")
    public void setCancelCutoffMinutes(int cancelCutoffMinutes) {
        this.cancelCutoffMinutes = cancelCutoffMinutes;
    }

}
