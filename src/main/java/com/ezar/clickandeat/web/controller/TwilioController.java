package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.TwilioServiceImpl;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.templating.VelocityTemplatingService;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowStatusException;
import com.ezar.clickandeat.workflow.WorkflowStatusExceptionMessageResolver;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class TwilioController implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(TwilioController.class);
    

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VelocityTemplatingService velocityTemplatingService;
    
    @Autowired
    private TwilioServiceImpl twilioService;

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;

    @Autowired
    private WorkflowStatusExceptionMessageResolver resolver;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    private String authKey;

    private String timeZone;

    private String locale;

    private Locale systemLocale;


    @Override
    public void afterPropertiesSet() throws Exception {
        String[] localeArray = locale.split("_");
        this.systemLocale = new Locale(localeArray[0],localeArray[1]);
    }

    /**
     * Receives request to make order notification
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.ORDER_NOTIFICATION_CALL_URL, method = RequestMethod.POST )
    public ResponseEntity<byte[]> orderNotificationCall(@RequestParam(value = "orderId", required = true) String orderId, 
                                                @RequestParam(value = "authKey", required = true) String authKey,
                                                HttpServletResponse response) throws Exception {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received request for order notification call for order id: " + orderId);
        }

        // Check authentication key passed
        checkAuthKey(authKey, response);

        // Get order from the request
        Order order = getOrder(orderId,response);

        // Build template options to return
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String url = twilioService.buildTwilioUrl(TwilioServiceImpl.FULL_ORDER_CALL_URL, orderId);
        templateModel.put("url", StringEscapeUtils.escapeHtml(url));
        templateModel.put("delivery",order.getDeliveryType().toLowerCase());
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.NOTIFICATION_CALL_TEMPLATE);

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generated xml response [" + xml + "]");
        }
        
        return responseEntityUtils.buildXmlResponse(xml);
    }


    /**
     * Callback for order notification call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.ORDER_NOTIFICATION_CALL_STATUS_CALLBACK_URL, method = RequestMethod.POST )
    public void orderNotificationCallStatusCallback(@RequestParam(value = "orderId", required = true) String orderId,
                                                   @RequestParam(value = "authKey", required = true) String authKey,
                                                   HttpServletResponse response, HttpServletRequest request) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received status callback for order notification call for order id: " + orderId);
        }

        // Check authentication key passed
        checkAuthKey(authKey, response);

        // Get order from the request
        Order order = getOrder(orderId,response);

        // Get call duration
        String callDurationParameter = request.getParameter("CallDuration");
        Integer callDuration = StringUtils.hasText(callDurationParameter)? Integer.valueOf(callDurationParameter): 0;
        String answeredBy = request.getParameter("AnsweredBy");

        // If no answer or answered by is 'Machine' send NO_ANSWER upate
        if( callDuration == 0 || "machine".equals(answeredBy)) {
            orderWorkflowEngine.processAction(order, OrderWorkflowEngine.ACTION_CALL_NOT_ANSWERED);
        }

        response.sendError(HttpServletResponse.SC_OK);
    }


    /**
     * Failure callback for order notification call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.ORDER_NOTIFICATION_CALL_FALLBACK_URL, method = RequestMethod.POST )
    public void orderNotificationCallFallback(@RequestParam(value = "orderId", required = true) String orderId,
                                                    @RequestParam(value = "authKey", required = true) String authKey,
                                                    HttpServletResponse response) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received error fallback for order notification call for order id: " + orderId);
        }

        // Check authentication key passed
        checkAuthKey(authKey, response);

        // Get order from the request
        Order order = getOrder(orderId,response);

        // Process error update for call
        orderWorkflowEngine.processAction(order, OrderWorkflowEngine.ACTION_CALL_ERROR);
        response.sendError(HttpServletResponse.SC_OK);
    }


    /**
     * Receives request to read out full order over the phone
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.FULL_ORDER_CALL_URL, method = RequestMethod.POST )
    public ResponseEntity<byte[]> fullOrderCall(@RequestParam(value = "orderId", required = true) String orderId,
                                                        @RequestParam(value = "authKey", required = true) String authKey,
                                                        HttpServletResponse response) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received request for full order call for order id: " + orderId);
        }

        // Check authentication key passed
        checkAuthKey(authKey, response);

        // Get order from the request
        Order order = getOrder(orderId,response);

        // Build template options to return
        String xml = buildFullOrderXml(order,false);

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generated xml response [" + xml + "]");
        }

        return responseEntityUtils.buildXmlResponse(xml);
    }


    /**
     * Callback for full order call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.FULL_ORDER_CALL_STATUS_CALLBACK_URL, method = RequestMethod.POST )
    public void fullOrderCallStatusCallback(@RequestParam(value = "orderId", required = true) String orderId,
                                                    @RequestParam(value = "authKey", required = true) String authKey,
                                                    HttpServletResponse response, HttpServletRequest request) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received status callback for full order call for order id: " + orderId);
        }

        // Check authentication key passed
        checkAuthKey(authKey, response);
        response.sendError(HttpServletResponse.SC_OK);
    }


    /**
     * Failure callback for order notification call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.FULL_ORDER_CALL_FALLBACK_URL, method = RequestMethod.POST )
    public void fullOrderCallFallback(@RequestParam(value = "orderId", required = true) String orderId,
                                              @RequestParam(value = "authKey", required = true) String authKey,
                                              HttpServletResponse response) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received error fallback for full order call for order id: " + orderId);
        }

        // Check authentication key passed
        checkAuthKey(authKey, response);

        // Get order from the request
        Order order = getOrder(orderId,response);

        // Process error update for call
        orderWorkflowEngine.processAction(order, OrderWorkflowEngine.ACTION_CALL_ERROR);
        response.sendError(HttpServletResponse.SC_OK);
    }


    /**
     * Callback for processing result of full order call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.FULL_ORDER_CALL_PROCESS_URL, method = RequestMethod.POST )
    public ResponseEntity<byte[]> fullOrderCallProcess(@RequestParam(value = "orderId", required = true) String orderId,
                                            @RequestParam(value = "authKey", required = true) String authKey,
                                            @RequestParam(value="Digits", required = true ) String digits,
                                            HttpServletResponse response, HttpServletRequest request) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received processing callback for full order call for order id: " + orderId);
        }

        // Check authentication key passed
        checkAuthKey(authKey, response);

        // Get order from the request
        Order order = getOrder(orderId,response);

        // Examine the digits returned from the call
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received digits " + digits + " as response to full order call");
        }
        
        // Check that a valid response was returned
        if( !StringUtils.hasText(digits)) {
            LOGGER.error("Did not receive keypad input from call");
            orderRepository.addOrderUpdate(orderId, "Did not receive any keypad input from full order call");
            
            // Generate the order call with an error
            String xml = buildFullOrderXml(order, true);
            return responseEntityUtils.buildXmlResponse(xml);
        }
        
        // Process response
        char firstDigit = digits.toCharArray()[0];
        switch(firstDigit) {
            
            // Order accepted
            case '1':

                try {
                    orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_CALL_ANSWERED);
                    orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_RESTAURANT_ACCEPTS);
                    return responseEntityUtils.buildXmlResponse(buildOrderCallResponseXml());
                }
                catch( WorkflowStatusException ex ) {
                    LOGGER.error(ex.getMessage(),ex);
                    String workflowError = resolver.getWorkflowStatusExceptionMessage(ex);
                    return responseEntityUtils.buildXmlResponse(buildErrorResponseXml(workflowError));
                }

            // Order rejected
            case '2':
                try {
                    orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_CALL_ANSWERED);
                    orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_RESTAURANT_DECLINES);
                    return responseEntityUtils.buildXmlResponse(buildOrderCallResponseXml());
                }
                catch( WorkflowStatusException ex ) {
                    LOGGER.error(ex.getMessage(),ex);
                    String workflowError = resolver.getWorkflowStatusExceptionMessage(ex);
                    return responseEntityUtils.buildXmlResponse(buildErrorResponseXml(workflowError));
                }

            // Order accepted with non-standard delivery time
            case '3':
                String deliveryMinutes = digits.substring(1);
                Map<String,Object> context = new HashMap<String, Object>();
                context.put("DeliveryMinutes",deliveryMinutes);
                try {
                    orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_CALL_ANSWERED);
                    orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_RESTAURANT_ACCEPTS_WITH_DELIVERY_DETAIL,context);
                    return responseEntityUtils.buildXmlResponse(buildOrderCallResponseXml());
                }
                catch( WorkflowStatusException ex ) {
                    LOGGER.error(ex.getMessage(),ex);
                    String workflowError = resolver.getWorkflowStatusExceptionMessage(ex);
                    return responseEntityUtils.buildXmlResponse(buildErrorResponseXml(workflowError));
                }


            // Repeat the call
            case '4':
                return responseEntityUtils.buildXmlResponse(buildFullOrderXml(order, false));

            // No response at this time
            case '5':
                orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_CALL_ANSWERED);
                return responseEntityUtils.buildXmlResponse(buildOrderCallResponseXml());

            // Invalid input
            default:
                LOGGER.error("Invalid response to full order call");
                return responseEntityUtils.buildXmlResponse(buildFullOrderXml(order, true));
        }
    }

    
    /**
     * @param authKey
     * @param response
     */
    
    private void checkAuthKey(String authKey, HttpServletResponse response ) throws IOException {
        if(!this.authKey.equals(authKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            throw new IllegalArgumentException("Invalid authentication key passed");
        }
    }


    /**
     * @param orderId
     * @param response
     * @return
     * @throws IOException
     */

    private Order getOrder(String orderId,HttpServletResponse response) throws IOException {
        Order order = orderRepository.findByOrderId(orderId);
        if( order == null ) {
            LOGGER.error("Could not find order with orderId: " + orderId);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            throw new IllegalArgumentException("No order found with orderId: " + orderId);
        }
        else {
            return order;
        }
    }


    /**
     * @param order
     * @param hasError
     * @return
     */
    
    private String buildFullOrderXml(Order order, boolean hasError) throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String url = twilioService.buildTwilioUrl(TwilioServiceImpl.FULL_ORDER_CALL_PROCESS_URL, order.getOrderId());
        templateModel.put("url", StringEscapeUtils.escapeHtml(url));
        templateModel.put("locale",systemLocale);
        templateModel.put("today",new LocalDate());
        templateModel.put("order",order);
        if(hasError) {
            templateModel.put("error",true);
        }
        return velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.FULL_ORDER_CALL_TEMPLATE);
    }


    /**
     * @return
     * @throws Exception
     */

    private String buildOrderCallResponseXml() throws Exception {
        String xml = velocityTemplatingService.mergeContentIntoTemplate(null, VelocityTemplatingService.FULL_ORDER_CALL_RESPONSE_TEMPLATE);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Generated xml [" + xml + "]");
        }
        return xml;
    }


    /**
     * @return
     * @throws Exception
     */

    private String buildErrorResponseXml(String error) throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        templateModel.put("error",error);
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.FULL_ORDER_CALL_WORKFLOW_ERROR_TEMPLATE);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Generated xml [" + xml + "]");
        }
        return xml;
    }


    @Required
    @Value(value="${twilio.authKey}")
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    
    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }


    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale;
    }

}
