package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.SecurityUtils;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowException;
import com.ezar.clickandeat.workflow.WorkflowStatusException;
import com.ezar.clickandeat.workflow.WorkflowStatusExceptionMessageResolver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller 
public class RestaurantEmailConfirmController implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantEmailConfirmController.class);
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private WorkflowStatusExceptionMessageResolver workflowStatusExceptionMessageResolver;

    private String locale;

    private Locale systemLocale;


    @Override
    public void afterPropertiesSet() throws Exception {
        String[] localeArray = locale.split("_");
        this.systemLocale = new Locale(localeArray[0],localeArray[1]);
    }


    @RequestMapping(value="/secure/workflow/confirmOrder.html", method= RequestMethod.GET)
    public ModelAndView acceptOrDeclineOrder(@RequestParam(value = "curl", required = true) String curl ) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();
        String orderId = null;
        String action;

        try {
            String unencoded = URLDecoder.decode(curl,"utf-8");
            String decrypted = securityUtils.decrypt(unencoded);
            String[] params = decrypted.split("#");
            orderId = params[0].split("=")[1];
            action = params[1].split("=")[1];
            
            Order order = orderRepository.findByOrderId(orderId);
            if( order == null ) {
                throw new IllegalArgumentException("Could not find order by orderId: " + orderId );
            }

            model.put("order",order);

            // Process the action
            try {
                order = orderWorkflowEngine.processAction(order,action);
                model.put("success",true);
                String orderStatusDescription = messageSource.getMessage("description." + order.getOrderStatus(),new Object[]{order.getRestaurant().getName()}, systemLocale);
                String message = messageSource.getMessage("workflow.update.message",new Object[]{order.getOrderId(),orderStatusDescription},systemLocale); 
                model.put("message",message);
            }
            catch( WorkflowStatusException ex ) {
                LOGGER.error("Workflow exception: " + ex.getMessage());
                model.put("success",false);
                String message = workflowStatusExceptionMessageResolver.getWorkflowStatusExceptionMessage(ex,systemLocale);
                model.put("message",message);
            }

        }
        catch( Exception ex ) {
            LOGGER.error("Execption for orderId: " + orderId,ex);
            model.put("success",false);
            String message = messageSource.getMessage("workflow.exception.message",new Object[]{ex.getMessage()},systemLocale);
            model.put("message",message);
        }

        return new ModelAndView("workflow/confirmOrder",model);
    }


    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale;
    }

}
