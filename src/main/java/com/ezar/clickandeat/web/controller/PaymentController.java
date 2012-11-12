package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Person;
import com.ezar.clickandeat.payment.PaymentService;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.web.controller.helper.RequestHelper;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Controller
public class PaymentController {
    
    private static final Logger LOGGER = Logger.getLogger(PaymentController.class);

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RequestHelper requestHelper;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    private String timeZone;

    
    @RequestMapping(value="/payment.html", method= RequestMethod.GET)
    public String payment(HttpServletRequest request) throws Exception {
        if( request.getSession(true).getAttribute("orderid") == null ) {
            return "redirect:/home.html";
        }
        Order order = requestHelper.getOrderFromSession(request);
        if( !order.getRestaurant().getTestMode()) {
            return "payment";
        }
        else {
            // Restaurant is in test mode, skip card payment
            order.setTestOrder(true);
            order.setTransactionId("DUMMY");
            order.setTransactionStatus(Order.PAYMENT_PRE_AUTHORISED);
            order.setAuthorisationCode("DUMMY");
            order.setSignature("DUMMY");
            order.setCardPaymentAmount(order.getTotalCost());
            order = orderRepository.saveOrder(order);

            // Send notification to restaurant and customer
            orderWorkflowEngine.processAction(order, ACTION_PLACE_ORDER);

            // Place order notification call
            try {
                orderWorkflowEngine.processAction(order,ACTION_CALL_RESTAURANT);
            }
            catch( Exception ex ) {
                orderWorkflowEngine.processAction(order, ACTION_CALL_ERROR);
            }

            // Clear session attributes
            HttpSession session = request.getSession(true);
            session.setAttribute("completedorderid",order.getOrderId());
            session.removeAttribute("orderid");
            session.removeAttribute("orderrestaurantid");
            session.removeAttribute("restaurantid");
            session.removeAttribute("cancheckout");

            // Send redirect to order confirmation page
            return "redirect:/orderSummary.html";
        }
    }


    @RequestMapping(value="/cardProcessing.html", method = RequestMethod.GET )
    public ModelAndView processCardPayment(HttpServletRequest request) throws Exception {
        Order order = requestHelper.getOrderFromSession(request);
        Map<String,String> model = paymentService.buildPaymentForm(order);
        return new ModelAndView("cardProcessing",model);
    }


    @RequestMapping(value="/paymentAccepted.html" )
    public ModelAndView paymentAccepted(HttpServletRequest request ) throws Exception {
        
        LOGGER.info("Received notification of successful card payment");
        Map<String,Object> model = new HashMap<String, Object>();
        
        // Get response
        String response = request.getParameter("Ds_Response");
        Integer responseCode = Integer.valueOf(response);
        
        // Responses over value of 99 are errors
        if( responseCode > 99 ) {
            String transactionError = MessageFactory.getMessage("payment.transaction.error_" + responseCode, false);
            if( transactionError == null ) {
                transactionError = MessageFactory.getMessage("payment.error-transaction-declined",false);
            }
            String error = MessageFactory.formatMessage("payment.transaction-error", false, transactionError);
            model.put("error",error);
            return new ModelAndView("payment",model);
        }
        
        // Extract payment details from response
        String orderId = request.getParameter("Ds_MerchantData");
        String transactionId = request.getParameter("Ds_Order");
        String authorisationCode = request.getParameter("Ds_AuthorisationCode");
        String signature = request.getParameter("Ds_Signature");
        String cardPaymentAmount = request.getParameter("Ds_Amount");

        // Get order from session and update payment details
        Order order = orderRepository.findByOrderId(orderId);
        order.setTransactionId(transactionId);
        order.setTransactionStatus(Order.PAYMENT_PRE_AUTHORISED);
        order.setAuthorisationCode(authorisationCode);
        order.setSignature(signature);
        order.setCardPaymentAmount(Double.valueOf(cardPaymentAmount) / 100d);
        order = orderRepository.saveOrder(order);

        // Send notification to restaurant and customer
        orderWorkflowEngine.processAction(order, ACTION_PLACE_ORDER);

        // Place order notification call
        try {
            orderWorkflowEngine.processAction(order,ACTION_CALL_RESTAURANT);
        }
        catch( Exception ex ) {
            orderWorkflowEngine.processAction(order, ACTION_CALL_ERROR);
        }

        // Clear session attributes
        HttpSession session = request.getSession(true);
        session.setAttribute("completedorderid",order.getOrderId());
        session.removeAttribute("orderid");
        session.removeAttribute("orderrestaurantid");
        session.removeAttribute("restaurantid");
        session.removeAttribute("cancheckout");

        // Send redirect to order confirmation page
        return new ModelAndView("paymentAccepted",model);
    }

    @RequestMapping(value="/paymentRejected.html" )
    public String paymentRejected() {
        return "paymentRejected";
    }


    @RequestMapping(value="/processPaymentRejected.html" )
    public ModelAndView processPaymentRejected(HttpServletRequest request) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        Order order = requestHelper.getOrderFromSession(request);
        if( order == null || !order.getCanCheckout()) {
            return new ModelAndView("redirect:/home.html",model);
        }

        order.setTransactionStatus(Order.PAYMENT_ERROR);
        orderRepository.saveOrder(order);
        
        String error = MessageFactory.getMessage("payment.general-error",false);
        model.put("error",error);
        return new ModelAndView("payment",model);
    }


    @RequestMapping(value="/approval/restaurant/testPhoneCall.html", method= RequestMethod.GET)
    //public String testPhoneCall(HttpServletRequest request) throws Exception {
    public ResponseEntity<byte[]> testPhoneCall(HttpServletRequest request) throws Exception {

        Map<String,Object> model = new HashMap<String,Object>();

        if( request.getSession(true).getAttribute("orderid") == null ) {
            model.put("success",false);
            model.put("message","No order has been created");
            return responseEntityUtils.buildResponse(model);
        }

        Order order = requestHelper.getOrderFromSession(request);

        // Update order delivery details
        order.setCustomer(new Person( MessageFactory.getMessage("twilio-test-user-name", false),"","6987857438","test@llamarycomer.com"));
        order.setDeliveryAddress(order.getRestaurant().getAddress());
        order.setAdditionalInstructions(MessageFactory.getMessage("twilio-test-success-message",false));
        order.setTermsAndConditionsAccepted(true);
        order.setOrderPlacedTime(new DateTime());
        order.addOrderUpdate("Order placed for testing");

        /* Restaurant is in test mode, skip card payment
        order.setTransactionId("DUMMY");
        order.setTransactionStatus(Order.PAYMENT_PRE_AUTHORISED);
        order.setAuthorisationCode("DUMMY");
        order.setSignature("DUMMY");
        order.setCardPaymentAmount(order.getTotalCost());
        order = orderRepository.saveOrder(order);
        */

        // Send notification to restaurant and customer
        // orderWorkflowEngine.processAction(order, ACTION_PLACE_ORDER);
        // Don't send the notification but change the order status to the value expected by the
        // next step in the workflow
        order.setOrderStatus(orderWorkflowEngine.ORDER_STATUS_AWAITING_RESTAURANT);
        // Place order notification call
        try {
            // flag the order to ignore the bit about the restaurant being open or not
            order.setIgnoreOpen(true);
            orderWorkflowEngine.processAction(order,ACTION_CALL_RESTAURANT);
            model.put("success",true);
            model.put("message",MessageFactory.getMessage("twilio-test-success-message",false));
            // Reset the order. I am not resetting the order because it will be handy to
            // be able to compare the phone call against the order
            // model.put("order",new Order());
        }
        catch( Exception ex ) {
            orderWorkflowEngine.processAction(order, ACTION_CALL_ERROR);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        // Clear session attributes
        HttpSession session = request.getSession(true);
        session.setAttribute("completedorderid",order.getOrderId());
        session.removeAttribute("orderid");
        session.removeAttribute("orderrestaurantid");
        session.removeAttribute("restaurantid");
        session.removeAttribute("cancheckout");

        // Flag the order as a test order
        order.setTestOrder(true);
        orderRepository.saveOrder(order);

        return responseEntityUtils.buildResponse(model);
    }



    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
