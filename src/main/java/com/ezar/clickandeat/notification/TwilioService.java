package com.ezar.clickandeat.notification;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
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

    private String fallbackUrl;
    
    private String statusCallbackUrl;

    private final Map<String,TwilioCallback> callbacks = new ConcurrentHashMap<String,TwilioCallback>();
    
    /**
     * @param phoneNumber
     * @param url
     */

    public String makeOrderNotification(String orderId, String phoneNumber, String url ) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Calling " + phoneNumber + " about order " + orderId + " with callback url " + url);
        }

        String callSid = null;
        
        try {
            // Create a rest client
            TwilioRestClient client = new TwilioRestClient(accountSid, authToken);
    
            // Get the main account (The one we used to authenticate the client
            Account mainAccount = client.getAccount();
    
            // Append the order id to the url
            if( url.endsWith("/")) {
                url = url.substring(0,url.lastIndexOf("/"));
            }
            url += "?orderId=" + orderId;
            
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
            Call call = callFactory.create(callParams);
            callSid = call.getSid();
    
            // Register callback and wait for response
            TwilioCallback callback = new TwilioCallback();
            callbacks.put(callSid,callback);
            String result = callback.getResult();
            
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("Received callback result: " + result);
            }
            return result;

        }
        catch( Exception ex ) {
            LOGGER.error("Exception occurred processing twilio request",ex);
            throw ex;
        }
        finally {
            if( callSid != null ) {
                callbacks.remove(callSid);
            }
        }
    }


    /**
     * @param callSid
     * @param digits
     */

    public void onResult(String callSid, String digits) {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received status callback on callSid: " + callSid);
        }
        
        TwilioCallback callback = callbacks.get(callSid);
        if( callback == null ) {
            LOGGER.error("No callback found for callSid: " + callSid);
        }
        else {
            callback.onResult(digits);
        }
    }


    /**
     * @param callSid
     * @param ex
     */

    public void onError(String callSid, Exception ex ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received exception for callSid: " + callSid);
        }

        TwilioCallback callback = callbacks.get(callSid);
        if( callback == null ) {
            LOGGER.error("No callback found for callSid: " + callSid);
        }
        else {
            callback.onError(ex);
        }
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
    @Value(value="${twilio.fallbackUrl}")
    public void setFallbackUrl(String fallbackUrl) {
        this.fallbackUrl = fallbackUrl;
    }

    @Required
    @Value(value="${twilio.statusCallbackUrl}")
    public void setStatusCallbackUrl(String statusCallbackUrl) {
        this.statusCallbackUrl = statusCallbackUrl;
    }


    /**
     * Callback class which waits for a result from the twilio server
     */
    private class TwilioCallback {

        private String digits;

        private Exception exception;

        private boolean responded = false;

        private final Object lock = new Object();


        /**
         * Waits for result or exception
         * @return
         * @throws Exception
         */

        private String getResult() throws Exception {
            while( !responded ) {
                try {
                    synchronized (lock) {
                        lock.wait();
                    }
                }
                catch( InterruptedException ex ) {
                    responded = true;
                    this.exception = ex;
                }
            }

            if( exception != null ) {
                throw(exception);
            }
            return digits;
        }


        /**
         * @param digits
         */

        public void onResult(String digits) {
            this.digits = digits;
            this.responded = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }


        /**
         * @param ex
         */

        public void onError(Exception ex) {
            this.exception = ex;
            this.responded = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

}



