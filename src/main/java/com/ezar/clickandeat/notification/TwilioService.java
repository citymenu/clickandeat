package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Call;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(value="twilioService")
public class TwilioService {

    private static final Logger LOGGER = Logger.getLogger(TwilioService.class);
    
    private String accountSid;
    
    private String authToken;

    private String callerId;

    private String orderNotificationSMSUrl;
    private String orderNotificationSMSFallbackUrl;
    private String orderNotificationSMSStatusCallbackUrl;

    private String orderNotificationCallUrl;
    private String orderNotificationCallFallbackUrl;
    private String orderNotificationCallStatusCallbackUrl;

    private String fullOrderCallUrl;
    private String fullOrderCallFallbackUrl;
    private String fullOrderCallStatusCallbackUrl;


    /**
     * @param order
     * @param restaurant
     * @throws Exception
     */

    public void sendOrderNotificationSMS(Order order, Restaurant restaurant ) throws Exception {

    }


    /**
     * @param order
     * @param restaurant
     * @return
     * @throws Exception
     */

    public void makeFullOrderCall(Order order, Restaurant restaurant) throws Exception {

        String phoneNumber = restaurant.getNotificationOptions().getNotificationPhoneNumber();
        String orderId = order.getOrderId();

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Calling " + phoneNumber + " to read details of order " + orderId);
        }

        placeOrderCall(orderId, phoneNumber, fullOrderCallUrl, fullOrderCallFallbackUrl, fullOrderCallStatusCallbackUrl);
    }


    /**
     * @param order
     * @param restaurant
     * @return
     * @throws Exception
     */
    
    public void makeOrderNotificationCall(Order order, Restaurant restaurant) throws Exception {

        String phoneNumber = restaurant.getNotificationOptions().getNotificationPhoneNumber();
        String orderId = order.getOrderId();
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Calling " + phoneNumber + " to notify about order " + orderId);
        }
        
        placeOrderCall(orderId, phoneNumber, orderNotificationCallUrl, orderNotificationCallFallbackUrl, orderNotificationCallStatusCallbackUrl);
    }






    /**
     * @param phoneNumber
     * @param url
     * @param fallbackUrl
     * @param statusCallbackUrl
     * @throws Exception
     */
    
    private void placeOrderCall(String orderId, String phoneNumber, String url, String fallbackUrl, String statusCallbackUrl ) throws Exception {

        // Create a rest client
        TwilioRestClient client = new TwilioRestClient(accountSid, authToken);

        // Get the main account (The one we used to authenticate the client
        Account mainAccount = client.getAccount();

        // Append the order id to the urls
        url += "?orderId=" + orderId;
        fallbackUrl += "?orderId=" + orderId;
        statusCallbackUrl += "?orderId=" + orderId; 

        // Build the call
        CallFactory callFactory = mainAccount.getCallFactory();
        Map<String, String> callParams = new HashMap<String, String>();
        callParams.put("To", phoneNumber);
        callParams.put("From", callerId);
        callParams.put("Url", url);
        callParams.put("Method", "GET");

        // Add the callback urls
        callParams.put("FallbackUrl", fallbackUrl);
        callParams.put("StatusCallback", statusCallbackUrl);

        // Place the call
        callFactory.create(callParams);
    }


    @Required
    @Value(value="${twilio.sid}")
    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    @Required
    @Value(value="${twilio.authKey}")
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Required
    @Value(value="${twilio.callerId}")
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    @Required
    @Value(value="${twilio.orderNotificationSMSUrl}")
    public void setOrderNotificationSMSUrl(String orderNotificationSMSUrl) {
        this.orderNotificationSMSUrl = orderNotificationSMSUrl;
    }

    @Required
    @Value(value="${twilio.orderNotificationSMSFallbackUrl}")
    public void setOrderNotificationSMSFallbackUrl(String orderNotificationSMSFallbackUrl) {
        this.orderNotificationSMSFallbackUrl = orderNotificationSMSFallbackUrl;
    }

    @Required
    @Value(value="${twilio.orderNotificationSMSStatusCallbackUrl}")
    public void setOrderNotificationSMSStatusCallbackUrl(String orderNotificationSMSStatusCallbackUrl) {
        this.orderNotificationSMSStatusCallbackUrl = orderNotificationSMSStatusCallbackUrl;
    }

    @Required
    @Value(value="${twilio.fullOrderCallUrl}")
    public void setFullOrderCallUrl(String fullOrderCallUrl) {
        this.fullOrderCallUrl = fullOrderCallUrl;
    }

    @Required
    @Value(value="${twilio.fullOrderCallFallbackUrl}")
    public void setFullOrderCallFallbackUrl(String fullOrderCallFallbackUrl) {
        this.fullOrderCallFallbackUrl = fullOrderCallFallbackUrl;
    }

    @Required
    @Value(value="${twilio.fullOrderCallStatusCallbackUrl}")
    public void setFullOrderCallStatusCallbackUrl(String fullOrderCallStatusCallbackUrl) {
        this.fullOrderCallStatusCallbackUrl = fullOrderCallStatusCallbackUrl;
    }

    @Required
    @Value(value="${twilio.orderNotificationCallUrl}")
    public void setOrderNotificationCallUrl(String orderNotificationCallUrl) {
        this.orderNotificationCallUrl = orderNotificationCallUrl;
    }

    @Required
    @Value(value="${twilio.orderNotificationCallFallbackUrl}")
    public void setOrderNotificationCallFallbackUrl(String orderNotificationCallFallbackUrl) {
        this.orderNotificationCallFallbackUrl = orderNotificationCallFallbackUrl;
    }

    @Required
    @Value(value="${twilio.orderNotificationCallStatusCallbackUrl}")
    public void setOrderNotificationCallStatusCallbackUrl(String orderNotificationCallStatusCallbackUrl) {
        this.orderNotificationCallStatusCallbackUrl = orderNotificationCallStatusCallbackUrl;
    }
}



