package com.ezar.clickandeat.templating;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.NumberTool;
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
    public static final String NOTIFICATION_SMS_TEMPLATE = "/velocity/twilio/{locale}/orderNotificationSMS.vm";
    public static final String NOTIFICATION_CALL_TEMPLATE = "/velocity/twilio/{locale}/orderNotificationCall.vm";
    public static final String FULL_ORDER_CALL_TEMPLATE = "/velocity/twilio/{locale}/fullOrderCall.vm";
    public static final String FULL_ORDER_CALL_RESPONSE_TEMPLATE = "/velocity/twilio/{locale}/fullOrderCallResponse.vm";

    // Email velocity template locations
    public static final String CUSTOMER_ORDER_CONFIRMATION_EMAIL_TEMPLATE = "/velocity/email/customerOrderConfirmation.vm";
    public static final String RESTAURANT_ORDER_NOTIFICATION_EMAIL_TEMPLATE = "/velocity/email/restaurantOrderNotification.vm";
    public static final String RESTAURANT_ACCEPTED_ORDER_EMAIL_TEMPLATE = "/velocity/email/restaurantAcceptedOrderConfirmation.vm";
    public static final String RESTAURANT_DECLINED_ORDER_EMAIL_TEMPLATE = "/velocity/email/restaurantDeclinedOrderConfirmation.vm";
    public static final String CUSTOMER_CANCELLED_ORDER_EMAIL_TEMPLATE = "/velocity/email/customerCancelledOrder.vm";
    public static final String CUSTOMER_CANCELLED_ORDER_CONFIRMATION_EMAIL_TEMPLATE = "/velocity/email/customerCancelledOrderConfirmation.vm";
    public static final String RESTAURANT_CANCELLED_ORDER_EMAIL_TEMPLATE = "/velocity/email/restaurantCancelledOrderConfirmation.vm";
    public static final String AUTO_CANCELLED_RESTAURANT_EMAIL_TEMPLATE = "/velocity/email/autoCancelledRestaurantConfirmation.vm";
    public static final String AUTO_CANCELLED_CUSTOMER_EMAIL_TEMPLATE = "/velocity/email/autoCancelledCustomerConfirmation.vm";
    public static final String CUSTOMER_CANCELLATION_OFFER_EMAIL_TEMPLATE = "/velocity/email/customerCancellationOffer.vm";
    public static final String RESTAURANT_DELISTED_EMAIL_TEMPLATE = "/velocity/email/restaurantDelistedNotification.vm";
    public static final String RESTAURANT_RELISTED_EMAIL_TEMPLATE = "/velocity/email/restaurantRelistedNotification.vm";

    // Velocity tools to be added to all contexts
    private Map<String,Object> velocityTools = new HashMap<String, Object>();
    
    private VelocityEngine engine;

    private String locale;
    
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
        velocityTools.put("numberTool", new NumberTool());
        velocityTools.put("stringTool", new StringTool());
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


    public static final class StringTool {

        public String unescape(Object obj) {
            if( obj == null ) {
                return null;
            }
            String str = (String)obj;
            return StringUtils.hasText(str)? str.replace("###","'"): str;
        }

        public boolean hasText(Object obj) {
            return obj != null && StringUtils.hasText((String)obj);
        }
    }
}
