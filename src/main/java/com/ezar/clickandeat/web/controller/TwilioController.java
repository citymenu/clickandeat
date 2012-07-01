package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.TwilioService;
import com.ezar.clickandeat.repository.OrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class TwilioController {

    private static final Logger LOGGER = Logger.getLogger(TwilioController.class);

    private static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><Response><Gather><Say>Please enter some digits</Say></Gather></Response>";

    @Autowired
    private OrderRepository orderRepository;

    private String authKey;
    
    /**
     * Receives request to make order notification
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value=TwilioService.ORDER_NOTIFICATION_CALL_URL, method = RequestMethod.GET )
    public ResponseEntity<byte[]> fullOrderCall(@RequestParam(value = "orderId", required = true) String orderId, 
                                                @RequestParam(value = "authKey", required = true) String authKey,
                                                HttpServletResponse response) throws Exception {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received request for order notification call for order id: " + orderId);
        }

        if(!this.authKey.equals(authKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        Order order = orderRepository.findByOrderId(orderId);
        if( order == null ) {
            LOGGER.error("Could not find order with orderId: " + orderId);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
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


    @Required
    @Value(value="${twilio.authKey}")
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }


}
