package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller 
public class RestaurantEmailConfirmController {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantEmailConfirmController.class);
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;


    @RequestMapping(value="/secure/workflow/confirmOrder.html", method= RequestMethod.GET)
    public ModelAndView acceptOrDeclineOrder(@RequestParam(value = "orderId", required = true) String orderId,
                                             @RequestParam(value = "uuid", required = true) String uuid,
                                             @RequestParam(value = "action", required = true) String action) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();
        try {
            Order order = orderRepository.findByOrderId(orderId);
            if( order == null ) {
                throw new IllegalArgumentException("Could not find order by orderId: " + orderId );
            }
            
            if( !order.getUuid().equals(uuid)) {
                throw new IllegalArgumentException("Invalid url for orderId: " + orderId);
            }
    
            // Process order workflow process
            model.put("order",order);
            orderWorkflowEngine.processAction(order,action);
            model.put("success",true);
        }
        catch( WorkflowException ex ) {
            LOGGER.error("Workflow execption for orderId: " + orderId, ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        catch( Exception ex ) {
            LOGGER.error("Execption for orderId: " + orderId,ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return new ModelAndView("workflow/confirmOrder",model);
    }
}
