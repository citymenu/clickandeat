package com.ezar.clickandeat.templating;

import com.ezar.clickandeat.config.MessageFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.NumberTool;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component(value="velocityTemplatingService")
public class VelocityTemplatingService implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(VelocityTemplatingService.class);

    private static final String VELOCITY_LOGGER_NAME = "VelocityLogger";

    // Twilio call velocity template locations
    public static final String ORDER_NOTIFICATION_SMS_TEMPLATE = "/velocity/twilio/{locale}/orderNotificationSMS.vm";
    public static final String RESTAURANT_DECLINED_NOTIFICATION_SMS_TEMPLATE = "/velocity/twilio/{locale}/restaurantDeclinedNotificationSMS.vm";
    public static final String AUTO_CANCELLATION_NOTIFICATION_SMS_TEMPLATE = "/velocity/twilio/{locale}/autoCancellationNotificationSMS.vm";
    public static final String NOTIFICATION_CALL_TEMPLATE = "/velocity/twilio/{locale}/orderNotificationCall.vm";
    public static final String FULL_ORDER_CALL_TEMPLATE = "/velocity/twilio/{locale}/fullOrderCall.vm";
    public static final String ORDER_DECLINED_RESPONSE_TEMPLATE = "/velocity/twilio/{locale}/fullOrderDeclinedResponse.vm";
    public static final String ORDER_ACCEPTED_RESPONSE_TEMPLATE = "/velocity/twilio/{locale}/fullOrderAcceptedResponse.vm";
    public static final String ORDER_ACCEPTED_WITH_DELIVERY_RESPONSE_TEMPLATE = "/velocity/twilio/{locale}/fullOrderAcceptedWithDeliveryResponse.vm";
    public static final String FULL_ORDER_CALL_WORKFLOW_ERROR_TEMPLATE = "/velocity/twilio/{locale}/fullOrderErrorResponse.vm";
    public static final String ORDER_CALL_ERROR_TEMPLATE = "/velocity/twilio/{locale}/errorResponse.vm";

    // Email velocity template locations
    public static final String CUSTOMER_ORDER_CONFIRMATION_EMAIL_TEMPLATE = "/velocity/email/{locale}/customerOrderConfirmation.vm";
    public static final String RESTAURANT_ORDER_NOTIFICATION_EMAIL_TEMPLATE = "/velocity/email/{locale}/restaurantOrderNotification.vm";
    public static final String RESTAURANT_ACCEPTED_ORDER_EMAIL_TEMPLATE = "/velocity/email/{locale}/restaurantAcceptedOrderConfirmation.vm";
    public static final String RESTAURANT_DECLINED_ORDER_EMAIL_TEMPLATE = "/velocity/email/{locale}/restaurantDeclinedOrderConfirmation.vm";
    public static final String CUSTOMER_CANCELLED_ORDER_EMAIL_TEMPLATE = "/velocity/email/{locale}/customerCancelledOrder.vm";
    public static final String CUSTOMER_CANCELLED_ORDER_CONFIRMATION_EMAIL_TEMPLATE = "/velocity/email/{locale}/customerCancelledOrderConfirmation.vm";
    public static final String SYSTEM_CANCELLED_ORDER_EMAIL_TEMPLATE = "/velocity/email/{locale}/systemCancelledOrderConfirmation.vm";
    public static final String AUTO_CANCELLED_RESTAURANT_EMAIL_TEMPLATE = "/velocity/email/{locale}/autoCancelledRestaurantConfirmation.vm";
    public static final String AUTO_CANCELLED_CUSTOMER_EMAIL_TEMPLATE = "/velocity/email/{locale}/autoCancelledCustomerConfirmation.vm";
    public static final String CUSTOMER_CANCELLATION_OFFER_EMAIL_TEMPLATE = "/velocity/email/{locale}/customerCancellationOffer.vm";
    public static final String RESTAURANT_DELISTED_EMAIL_TEMPLATE = "/velocity/email/{locale}/restaurantDelistedNotification.vm";
    public static final String RESTAURANT_RELISTED_EMAIL_TEMPLATE = "/velocity/email/{locale}/restaurantRelistedNotification.vm";
    public static final String OWNER_CONTENT_APPROVAL_EMAIL_TEMPLATE = "/velocity/email/{locale}/restaurantOwnerContentApproval.vm";
    public static final String OWNER_CONTENT_APPROVED_EMAIL_TEMPLATE = "/velocity/email/{locale}/restaurantContentApproved.vm";
    public static final String OWNER_CONTENT_REJECTED_EMAIL_TEMPLATE = "/velocity/email/{locale}/restaurantContentRejected.vm";

    // Velocity tools to be added to all contexts
    private Map<String,Object> velocityTools = new HashMap<String, Object>();
    
    private VelocityEngine engine;

    private String locale;

    private String baseUrl;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        engine.setProperty("runtime.log.logsystem.log4j.logger",VELOCITY_LOGGER_NAME);
        engine.setProperty("resource.loader","class");
        engine.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        engine.setProperty("velocimacro.library.autoreload","false");
        engine.setProperty("file.resource.loader.cache","true");
        engine.setProperty("file.resource.loader.modificationCheckInterval","-1");
        engine.setProperty("parser.pool.size","20");
        engine.init();

        // Add velocity tools to map
        Map<String,String> params = new HashMap<String, String>();
        params.put(NumberTool.DEFAULT_LOCALE_KEY, locale);
        NumberTool numberTool = new NumberTool();
        numberTool.configure(params);
        velocityTools.put("numberTool", numberTool);
        velocityTools.put("stringTool", new StringTool());
        velocityTools.put("twilioTool", new TwilioTool());
    }


    /**
     * @param model
     * @param templateLocation
     * @return
     * @throws Exception
     */
    
    public String mergeContentIntoTemplate(Map<String,Object> model, String templateLocation) throws Exception {
    
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Merging content into template location: " + templateLocation);
        }

        VelocityContext context = new VelocityContext();
        if( model != null ) {
            for(Map.Entry<String,Object> entry: model.entrySet()) {
                context.put(entry.getKey(),entry.getValue());
            }
        }
        for( Map.Entry<String,Object> entry: velocityTools.entrySet()) {
            context.put(entry.getKey(),entry.getValue());
        }
        
        // Add default useful objects
        context.put("locale",MessageFactory.getLocale());
        context.put("language", MessageFactory.getLanguage());
        context.put("country", MessageFactory.getCountry());
        context.put("today",new LocalDate());
        context.put("baseUrl",baseUrl);

        StringWriter sw = new StringWriter();
        engine.mergeTemplate(getLocaleTemplate(templateLocation),"utf-8",context,sw);
        return sw.toString();
    }


    /**
     * @param templateLocation
     * @return
     */

    private String getLocaleTemplate( String templateLocation ) {
        return templateLocation.replace("{locale}",locale);
    }


    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale;
    }

    
    @Required
    @Value(value="${baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Twilio util class.
    public static final class TwilioTool {

        // Amount of characters a waiter can write per second
        Integer charsPerSec = new Integer( MessageFactory.getMessage("twilio.chars-per-sec", true));

        // Seconds we want Twilio to pause for based on the length of the text read
        public String getPauseValue(Object obj) {

            if( obj == null ) {
                return null;
            }

            Integer pauseLength = new Integer(Math.round(((String) obj).length() / charsPerSec.intValue()));
            if (pauseLength==0) {
                pauseLength = 1;
            }
            return pauseLength.toString();
        }

        public String getOrderId(Object obj){
            return (((String)obj).replaceFirst("^0+(?!$)", "")).replaceAll("\\B", ", ");
        }

        // This is needed to read numbers as digits.
        // Used for Spanish telephones and postcodes
        public String getDigits(Object obj){
            return ((String)obj).replaceAll("\\B", ", ");
        }

    }


    public static final class StringTool {

        public String escape(Object obj) {
            return escape(obj,false);
        }

        public String escapeXml(Object obj) {
            if( obj == null ) {
                return null;
            }
            return StringEscapeUtils.escapeXml((String) obj);

        }
        
        public String escape(Object obj, boolean escapeNewLines) {
            if( obj == null ) {
                return null;
            }
            String escaped = StringEscapeUtils.escapeHtml((String)obj);
            if( escapeNewLines ) {
                escaped = escaped.replace("\n","<br>").replace(" ","&nbsp;");
            }
            return escaped;
        }

        public boolean hasText(Object obj) {
            return obj != null && StringUtils.hasText((String)obj);
        }
        
        public String formatDate(DateTime dateTime) {
            if( dateTime == null ) {
                return null;
            }
            return DateTimeFormat.forPattern("HH:mm").print(dateTime);
        }

        public String formatMessage(String key, String value) {
            return MessageFactory.formatMessage(key,true,value);
        }

        public String getMessage(String key) {
            return MessageFactory.getMessage(key, true);
        }
    }
}
