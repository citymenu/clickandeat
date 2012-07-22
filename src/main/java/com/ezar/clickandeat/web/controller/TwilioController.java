package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.TwilioService;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.templating.VelocityTemplatingService;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import org.apache.commons.lang.StringEscapeUtils;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TwilioController {

    private static final Logger LOGGER = Logger.getLogger(TwilioController.class);
    
    private static final String NOTIFICATION_CALL_TEMPLATE = "/velocity/twilio/orderNotificationCall.vm";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VelocityTemplatingService velocityTemplatingService;
    
    @Autowired
    private TwilioService twilioService;
    
    private String authKey;
    
    /**
     * Receives request to make order notification
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value=TwilioService.ORDER_NOTIFICATION_CALL_URL, method = RequestMethod.POST )
    public ResponseEntity<byte[]> orderNotificationCall(@RequestParam(value = "orderId", required = true) String orderId, 
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
            throw new IllegalArgumentException("No order found with orderId: " + orderId);
        }

        // Build template options to return 
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String url = twilioService.buildTwilioUrl(TwilioService.FULL_ORDER_CALL_URL, orderId);
        templateModel.put("url", StringEscapeUtils.escapeHtml(url));
        templateModel.put("delivery",order.getDeliveryType().toLowerCase());
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, NOTIFICATION_CALL_TEMPLATE);

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generated xml response [" + xml + "]");
        }
        
        return ResponseEntityUtils.buildXmlResponse(xml);
    }


    /**
     * Callback for order notification call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value=TwilioService.ORDER_NOTIFICATION_CALL_STATUS_CALLBACK_URL, method = RequestMethod.POST )
    public void orderNotificationCallStatusCallback(@RequestParam(value = "orderId", required = true) String orderId,
                                                   @RequestParam(value = "authKey", required = true) String authKey,
                                                   HttpServletResponse response, HttpServletRequest request) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received status callback for order notification call for order id: " + orderId);
        }

        if(!this.authKey.equals(authKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            throw new IllegalArgumentException("Invalid authentication key passed");
        }

        orderRepository.addOrderUpdate(orderId,"Received callback for successful order notification call");
        response.sendError(HttpServletResponse.SC_OK);
    }


    /**
     * Failure callback for order notification call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value=TwilioService.ORDER_NOTIFICATION_CALL_FALLBACK_URL, method = RequestMethod.POST )
    public void orderNotificationCallFallback(@RequestParam(value = "orderId", required = true) String orderId,
                                                    @RequestParam(value = "authKey", required = true) String authKey,
                                                    HttpServletResponse response) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received error fallback for order notification call for order id: " + orderId);
        }

        if(!this.authKey.equals(authKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            throw new IllegalArgumentException("Invalid authentication key passed");
        }

        orderRepository.addOrderUpdate(orderId,"Received callback for error in order notification call");
        response.sendError(HttpServletResponse.SC_OK);
    }




    @Required
    @Value(value="${twilio.authKey}")
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }


}
