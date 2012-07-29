package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderNotificationCallPlacedHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(OrderNotificationCallPlacedHandler.class);

    @Override
    public String getWorkflowAction() {
        return OrderWorkflowEngine.ACTION_NOTIFICATION_CALL_PLACED;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Placed order notification call to restaurant");
        order.setOrderNotificationCallCount(order.getOrderNotificationCallCount() + 1 );
        return order;
    }

}
