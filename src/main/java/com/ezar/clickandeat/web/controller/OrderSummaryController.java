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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Controller
public class OrderSummaryController {
    
    private static final Logger LOGGER = Logger.getLogger(OrderSummaryController.class);

    @Autowired
    private OrderRepository orderRepository;
    
    @RequestMapping(value="/orderSummary.html", method= RequestMethod.GET)
    public ModelAndView orderSummary(HttpServletRequest request ) throws Exception {
        Map<String,Object> model = new HashMap<String, Object>();
        String completedOrderId = (String)request.getSession(true).getAttribute("completedorderid");
        if( completedOrderId == null ) {
            return new ModelAndView("redirect:/home.html",model);
        }
        else {
            model.put("order",orderRepository.findByOrderId(completedOrderId));
            return new ModelAndView("orderSummary",model);
        }
    }

}
