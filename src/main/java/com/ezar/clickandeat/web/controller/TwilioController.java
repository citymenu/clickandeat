package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.TwilioServiceImpl;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.templating.VelocityTemplatingService;
import com.ezar.clickandeat.util.DistributedLockFactory;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowStatusException;
import com.ezar.clickandeat.workflow.WorkflowStatusExceptionMessageResolver;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
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

    @Autowired
    private DistributedLockFactory lockFactory;
    
    private String authKey;

    private String locale;

    private Locale systemLocale;


    @Override
    public void afterPropertiesSet() throws Exception {
        String[] localeArray = locale.split("_");
        this.systemLocale = new Locale(localeArray[0],localeArray[1]);
    }


    /**
     * Initiate order call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.ORDER_NOTIFICATION_CALL_URL, method = RequestMethod.POST )
    public ResponseEntity<byte[]> orderCallRequest(@RequestParam(value = "orderId", required = true) String orderId,
                                                @RequestParam(value = "authKey", required = true) String authKey,
                                                HttpServletResponse response) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received request for order call for order id: " + orderId);
        }

        // Check authentication key passed
        checkAuthKey(authKey, response);

        // Get order from the request
        Order order = getOrder(orderId,response);

        // Build template options to return
        String xml = buildOrderIntroductionXml(order,false);

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generated xml response [" + xml + "]");
        }

        return responseEntityUtils.buildXmlResponse(xml);
    }


    /**
     * Callback for processing result of full order call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.ORDER_NOTIFICATION_CALL_PROCESS_URL, method = RequestMethod.POST )
    public ResponseEntity<byte[]> orderCallProcess(@RequestParam(value = "orderId", required = true) String orderId,
                                                       @RequestParam(value = "authKey", required = true) String authKey,
                                                       @RequestParam(value="Digits", required = true ) String digits,
                                                       HttpServletResponse response, HttpServletRequest request) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received processing callback for order call for order id: " + orderId);
        }

        try {
        
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
                orderRepository.addOrderUpdate(orderId, "Did not receive any keypad input from order notification call");
    
                // Generate the order call with an error
                String xml = buildOrderIntroductionXml(order, true);
                return responseEntityUtils.buildXmlResponse(xml);
            }
    
            // Process response
            char firstDigit = digits.toCharArray()[0];
            switch(firstDigit) {
    
                // Order declined
                case '0':
                    try {
                        orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_CALL_ANSWERED);
                        orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_RESTAURANT_DECLINES);
                        return responseEntityUtils.buildXmlResponse(buildDeclinedResponseXml());
                    }
                    catch( WorkflowStatusException ex ) {
                        LOGGER.error(ex.getMessage(),ex);
                        String workflowError = resolver.getWorkflowStatusExceptionMessage(ex);
                        return responseEntityUtils.buildXmlResponse(buildErrorResponseXml(workflowError));
                    }
                // Order accepted
                case '1':
                    try {
                        orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_CALL_ANSWERED);
                        orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_RESTAURANT_ACCEPTS);
                        return responseEntityUtils.buildXmlResponse(buildAcceptedResponseXml());
                    }
                    catch( WorkflowStatusException ex ) {
                        LOGGER.error(ex.getMessage(),ex);
                        String workflowError = resolver.getWorkflowStatusExceptionMessage(ex);
                        return responseEntityUtils.buildXmlResponse(buildErrorResponseXml(workflowError));
                    }
                    // Hear order item details
                case '2':
                    return responseEntityUtils.buildXmlResponse(buildOrderItemsXml(order));
    
                // Hear order delivery details
                case '3':
                    return responseEntityUtils.buildXmlResponse(buildOrderDeliveryXml(order));
    
                // Order accepted with non-standard delivery time
                case '4':
                    String deliveryMinutesText = digits.substring(1);
    
                    // Try to extract the delivery minutes from the input
                    int deliveryMinutes;
                    try {
                        deliveryMinutes = Integer.valueOf(deliveryMinutesText);
                        if(deliveryMinutes <= 0) {
                            throw new IllegalArgumentException("Invalid delivery minutes: " + deliveryMinutes);
                        }
                    }
                    catch( Exception ex ) {
                        LOGGER.error("Could not parse delivery time minutes: " + ex.getMessage());
                        return responseEntityUtils.buildXmlResponse(buildOrderIntroductionXml(order, true));
                    }
    
                    Map<String,Object> context = new HashMap<String, Object>();
                    context.put("DeliveryMinutes",deliveryMinutes);
                    try {
                        orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_CALL_ANSWERED);
                        orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_RESTAURANT_ACCEPTS_WITH_DELIVERY_DETAIL,context);
                        return responseEntityUtils.buildXmlResponse(buildAcceptedWithDeliveryResponseXml(deliveryMinutes));
                    }
                    catch( WorkflowStatusException ex ) {
                        LOGGER.error(ex.getMessage(),ex);
                        String workflowError = resolver.getWorkflowStatusExceptionMessage(ex);
                        return responseEntityUtils.buildXmlResponse(buildErrorResponseXml(workflowError));
                    }
    
    
                // Call acknowledged
                case '5':
                    try {
                        orderWorkflowEngine.processAction(order,OrderWorkflowEngine.ACTION_CALL_ANSWERED);
                        return responseEntityUtils.buildXmlResponse(buildAnsweredResponseXml());
                    }
                    catch( WorkflowStatusException ex ) {
                        LOGGER.error(ex.getMessage(),ex);
                        String workflowError = resolver.getWorkflowStatusExceptionMessage(ex);
                        return responseEntityUtils.buildXmlResponse(buildErrorResponseXml(workflowError));
                    }
    
                // Invalid input
                default:
                    LOGGER.error("Invalid response to full order call");
                    return responseEntityUtils.buildXmlResponse(buildOrderIntroductionXml(order, true));
            }
        }
        finally {
            releaseCallLock(orderId);
        }
    }


    /**
     * Callback for order call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.ORDER_NOTIFICATION_CALL_STATUS_CALLBACK_URL, method = RequestMethod.POST )
    public void orderCallStatusCallback(@RequestParam(value = "orderId", required = true) String orderId,
                                            @RequestParam(value = "authKey", required = true) String authKey,
                                            HttpServletResponse response, HttpServletRequest request) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received status callback for order call for order id: " + orderId);
        }

        try {
            // Check authentication key passed
            checkAuthKey(authKey, response);

            // Get order from the request
            Order order = getOrder(orderId,response);

            // Get call status and duration
            String callStatus = request.getParameter("CallStatus");
            String callDurationParameter = request.getParameter("CallDuration");
            Integer callDuration = StringUtils.hasText(callDurationParameter)? Integer.valueOf(callDurationParameter): 0;
            LOGGER.info("Order id: " + orderId + ", call status: " + callStatus + ", duration: " + callDuration);

            // If line busy do not count as a missed call attempt
            if( callStatus.equals("busy")) {
                orderWorkflowEngine.processAction(order, OrderWorkflowEngine.ACTION_CALL_LINE_BUSY);
            }
            else if( !callStatus.equals("completed") || callDuration == 0 ) {
                orderWorkflowEngine.processAction(order, OrderWorkflowEngine.ACTION_CALL_NOT_ANSWERED);
            }
            else {
                orderWorkflowEngine.processAction(order, OrderWorkflowEngine.ACTION_CALL_ANSWERED);
            }
            response.sendError(HttpServletResponse.SC_OK);
        }
        finally {
            releaseCallLock(orderId);
        }
    }


    /**
     * Failure callback for order call
     * @param orderId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value= TwilioServiceImpl.ORDER_NOTIFICATION_CALL_FALLBACK_URL, method = RequestMethod.POST )
    public ResponseEntity<byte[]> orderCallFallback(@RequestParam(value = "orderId", required = true) String orderId,
                                                        @RequestParam(value = "authKey", required = true) String authKey,
                                                        HttpServletResponse response) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received error fallback for order call for order id: " + orderId);
        }

        try {
            // Check authentication key passed
            checkAuthKey(authKey, response);

            // Get order from the request
            Order order = getOrder(orderId,response);

            // Process error update for call
            orderWorkflowEngine.processAction(order, OrderWorkflowEngine.ACTION_CALL_ERROR);

            // Build response to call
            Map<String,Object> templateModel = new HashMap<String, Object>();
            String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_ERROR_TEMPLATE);
            return responseEntityUtils.buildXmlResponse(xml);
        }
        finally {
            releaseCallLock(orderId);
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

    private String buildOrderIntroductionXml(Order order, boolean hasError) throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String url = twilioService.buildTwilioUrl(TwilioServiceImpl.ORDER_NOTIFICATION_CALL_PROCESS_URL, order.getOrderId());
        templateModel.put("url", StringEscapeUtils.escapeHtml(url));
        templateModel.put("locale",systemLocale);
        templateModel.put("today",new LocalDate());
        templateModel.put("order",order);
        if(hasError) {
            templateModel.put("error",true);
        }
        return velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_INTRODUCTION_CALL_TEMPLATE);
    }


    /**
     * @param order
     * @return
     */

    private String buildOrderItemsXml(Order order) throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String url = twilioService.buildTwilioUrl(TwilioServiceImpl.ORDER_NOTIFICATION_CALL_PROCESS_URL, order.getOrderId());
        templateModel.put("url", StringEscapeUtils.escapeHtml(url));
        templateModel.put("locale",systemLocale);
        templateModel.put("today",new LocalDate());
        templateModel.put("order",order);
        return velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_ITEM_DETAILS_CALL_TEMPLATE);
    }


    /**
     * @param order
     * @return
     */

    private String buildOrderDeliveryXml(Order order) throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String url = twilioService.buildTwilioUrl(TwilioServiceImpl.ORDER_NOTIFICATION_CALL_PROCESS_URL, order.getOrderId());
        templateModel.put("url", StringEscapeUtils.escapeHtml(url));
        templateModel.put("locale",systemLocale);
        templateModel.put("today",new LocalDate());
        templateModel.put("order",order);
        return velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_DELIVERY_DETAILS_CALL_TEMPLATE);
    }


    /**
     * @return
     * @throws Exception
     */
    
    private String buildDeclinedResponseXml() throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_DECLINED_RESPONSE_TEMPLATE);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Generated xml [" + xml + "]");
        }
        return xml;
    }


    /**
     * @return
     * @throws Exception
     */

    private String buildAcceptedResponseXml() throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_ACCEPTED_RESPONSE_TEMPLATE);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Generated xml [" + xml + "]");
        }
        return xml;
    }


    /**
     * @param deliveryMinutes
     * @return
     * @throws Exception
     */
    
    private String buildAcceptedWithDeliveryResponseXml(Integer deliveryMinutes) throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        templateModel.put("DeliveryMinutes",deliveryMinutes);
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_ACCEPTED_WITH_DELIVERY_RESPONSE_TEMPLATE);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Generated xml [" + xml + "]");
        }
        return xml;
    }


    /**
     * @return
     * @throws Exception
     */

    private String buildAnsweredResponseXml() throws Exception {
        Map<String,Object> templateModel = new HashMap<String, Object>();
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_ANSWERED_RESPONSE_TEMPLATE);
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
        String xml = velocityTemplatingService.mergeContentIntoTemplate(templateModel, VelocityTemplatingService.ORDER_WORKFLOW_ERROR_TEMPLATE);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Generated xml [" + xml + "]");
        }
        return xml;
    }


    /**
     * @param orderId
     */

    private void releaseCallLock( String orderId ) {
        try {
            lockFactory.release(orderId);
        }
        catch( Exception ex) {
            LOGGER.error("Error occurred releasing call lock for order id: " + orderId,ex);
            lockFactory.remove(orderId);
        }
    }


    @Required
    @Value(value="${twilio.authKey}")
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    
    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale;
    }

}
