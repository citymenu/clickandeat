package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderNotificationCallAnsweredHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(OrderNotificationCallAnsweredHandler.class);

    @Override
    public String getWorkflowAction() {
        return OrderWorkflowEngine.ACTION_NOTIFICATION_CALL_ANSWERED;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Restaurant answered order notification call");
        order.setOrderNotificationStatus(OrderWorkflowEngine.NOTIFICATION_CALL_STATUS_RESTAURANT_ANSWERED);
        return order;
    }

}
