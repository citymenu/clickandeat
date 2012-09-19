package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.web.controller.helper.RequestHelper;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    private RequestHelper requestHelper;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    private String timeZone;

    
    @RequestMapping(value="/secure/payment.html", method= RequestMethod.GET)
    public String payment(HttpServletRequest request) throws Exception {

        if( request.getSession(true).getAttribute("orderid") == null ) {
            return "redirect:/home.html";
        }

        return "payment";
    }


    @ResponseBody
    @RequestMapping(value="/secure/processCardPayment.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> processCardPayment(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();
        
        try {
            Order order = requestHelper.getOrderFromSession(request);

            //TODO get credit card details and handle payment processing/result
            order.setCardTransactionId("12345");
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

            // Add completed order id to the session
            request.getSession(true).setAttribute("completedorderid",order.getOrderId());
            request.getSession(true).removeAttribute("orderid");
            request.getSession(true).removeAttribute("restaurantid");
            request.getSession(true).removeAttribute("cancheckout");

            // Set status to success
            model.put("success",true);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        return responseEntityUtils.buildResponse(model);

    }


    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
