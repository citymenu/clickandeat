package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderNotificationCallErrorHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(OrderNotificationCallErrorHandler.class);

    @Override
    public String getWorkflowAction() {
        return OrderWorkflowEngine.ACTION_NOTIFICATION_CALL_ERROR;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Error occurred placing notification call to restaurant");
        order.setOrderNotificationStatus(OrderWorkflowEngine.NOTIFICATION_CALL_STATUS_ERROR);
        return order;
    }

}
