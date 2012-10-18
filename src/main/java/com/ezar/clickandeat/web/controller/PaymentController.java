package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.payment.PaymentService;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.web.controller.helper.RequestHelper;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
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
        return "payment";
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
        order.setTransactionStatus(Order.PAYMENT_AUTHORISED);
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


    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
