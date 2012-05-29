package com.ezar.clickandeat.notification;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;
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
    public void testMakeCall() throws Exception {
        String phoneNumber = "+442085057191";
        String url = "http://clickandeat.herokuapp.com/twilioRequest.html";
        String callSid = twilioService.makeCall(phoneNumber,url);
        LOGGER.info("Received callSid " + callSid );
        Assert.assertNotNull("callSid should not be null",callSid);
    }
    
    
}
