package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
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
    public String paymentAccepted(HttpServletRequest request ) throws Exception {
        
        LOGGER.info("Received notification of successful card payment");

        // Extract payment details from response
        String orderId = request.getParameter("Ds_MerchantData");
        String transactionId = request.getParameter("Ds_Merchant_Order");
        String authorisationCode = request.getParameter("Ds_AuthorisationCode");
        String signature = request.getParameter("Ds_Signature");
        String cardPaymentAmount = request.getParameter("Ds_Amount");

        // Get order from session and update payment details
        Order order = orderRepository.findByOrderId(orderId);
        order.setTransactionId(transactionId);
        order.setTransactionStatus(Order.AUTHORISED);
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
        return "paymentAccepted";
    }


    @RequestMapping(value="/paymentRejected.html" )
    public void processPaymentRejected(HttpServletRequest request ) throws Exception {
        LOGGER.info("Got rejected transaction");
    }


    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
