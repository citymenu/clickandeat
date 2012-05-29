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

@Component(value="twilioService")
public class TwilioService {

    private static final Logger LOGGER = Logger.getLogger(TwilioService.class);
    
    private String accountSid;
    
    private String authToken;

    private String callerId;

    private String fallbackUrl;
    
    private String statusCallbackUrl;

    /**
     * @param phoneNumber
     * @param url
     */

    public String makeCall(String phoneNumber, String url ) throws TwilioRestException {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Calling " + phoneNumber + " with callback url " + url);
        }

        try {
        
            // Create a rest client
            TwilioRestClient client = new TwilioRestClient(accountSid, authToken);

            // Get the main account (The one we used to authenticate the client
            Account mainAccount = client.getAccount();

            // Build the call
            CallFactory callFactory = mainAccount.getCallFactory();
            Map<String, String> callParams = new HashMap<String, String>();
            callParams.put("To", phoneNumber);
            callParams.put("From", callerId);
            callParams.put("Url", url);

            // Add the callback urls
            //callParams.put("FallbackUrl");
            //callParams.put("StatusCallback");


            // Place the call
            Call call = callFactory.create(callParams);
            return call.getSid();

        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            throw new RuntimeException(ex);
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
}



