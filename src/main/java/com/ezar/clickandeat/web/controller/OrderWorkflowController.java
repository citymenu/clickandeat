package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.repository.OrderRepository;
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
public class OrderWorkflowController {
    
    private static final Logger LOGGER = Logger.getLogger(OrderWorkflowController.class);
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value="/secure/workflow/confirmOrder.html", method= RequestMethod.GET)
    public ModelAndView acceptOrDeclineOrder(@RequestParam(value = "orderId", required = true) String orderId,
                                             @RequestParam(value = "uuid", required = true) String uuid,
                                             @RequestParam(value = "action", required = true) String action) throws Exception {
        
        Order order = orderRepository.findByOrderId(orderId);
        if( order == null ) {
            throw new IllegalArgumentException("Could not find order by orderId: " + orderId );
        }
        
        if( !order.getUuid().equals(uuid)) {
            throw new IllegalArgumentException("Invalid url for orderId: " + orderId);
        }

        //TODO check current order status
        
        if( "accept".equals(action)) {
            order.setOrderStatus(Order.RESTAURANT_ACCEPTED);
            //notificationService.sendOrderAcceptedConfirmationToCustomer(order);
        }
        else if( "decline".equals(action)) {
            order.setOrderStatus(Order.RESTAURANT_DECLINED);
            //notificationService.sendOrderDeclinedConfirmationToCustomer(order);
        }

        order = orderRepository.saveOrder(order);

        Map<String,Object> model = new HashMap<String, Object>();
        model.put("order",order);

        return new ModelAndView("workflow/confirmOrder",model);
        
    }
}
