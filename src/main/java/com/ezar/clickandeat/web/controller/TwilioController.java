package com.ezar.clickandeat.web.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TwilioController {

    private static final Logger LOGGER = Logger.getLogger(TwilioController.class);

    private static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><Response><Say>You big monkey nuts</Say></Response>";

    @ResponseBody
    @RequestMapping(value="/twilioRequest.html", method = RequestMethod.GET )
    public ResponseEntity<byte[]> request(@RequestParam(value = "orderId", required = false) String orderId ) throws Exception {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received request for twilio document for order id: " + orderId);
        }

        final HttpHeaders headers = new HttpHeaders();
        final byte[] bytes = xml.getBytes("utf-8");
        headers.setContentType(MediaType.TEXT_XML);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);

    }

}
