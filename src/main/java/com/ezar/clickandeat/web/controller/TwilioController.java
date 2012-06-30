package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.notification.TwilioService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class TwilioController {

    private static final Logger LOGGER = Logger.getLogger(TwilioController.class);

    private static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><Response><Gather><Say>Please enter some digits</Say></Gather></Response>";

    @Autowired
    private TwilioService twilioService;

    
    @ResponseBody
    @RequestMapping(value="/twilioRequest.html", method = RequestMethod.GET )
    public ResponseEntity<byte[]> request(@RequestParam(value = "orderId", required = true) String orderId ) throws Exception {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received request for twilio document for order id: " + orderId);
        }

        final HttpHeaders headers = new HttpHeaders();
        final byte[] bytes = xml.getBytes("utf-8");
        headers.setContentType(MediaType.TEXT_XML);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);

    }


    @ResponseBody
    @RequestMapping(value="/twilioCallback.html", method = RequestMethod.POST )
    @ResponseStatus( HttpStatus.OK )
    public void callback(@RequestParam(value = "CallSid", required = false) String callSid, @RequestParam(value = "Digits", required = false) String digits ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received callback from twilio for callSid: " + callSid);
        }

        LOGGER.debug("Received digits from call as: " + digits);
    }


    @ResponseBody
    @RequestMapping(value="/twilioError.html", method = RequestMethod.POST )
    @ResponseStatus( HttpStatus.OK )
    public void error(@RequestParam(value = "CallSid", required = false) String callSid ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received error from twilio for callSid: " + callSid);
        }
    }

}
