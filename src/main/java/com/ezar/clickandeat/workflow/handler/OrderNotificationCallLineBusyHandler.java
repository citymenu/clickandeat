package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_CALL_LINE_BUSY;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.NOTIFICATION_STATUS_RESTAURANT_NO_ANSWER;

@Component
public class OrderNotificationCallLineBusyHandler implements IWorkflowHandler {
    
    @Override
    public String getWorkflowAction() {
        return ACTION_CALL_LINE_BUSY;
    }

    @Override
    public boolean isActionValidForOrder(Order order) {
        return true;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Line busy making order notification call to restaurant");
        order.setOrderNotificationStatus(NOTIFICATION_STATUS_RESTAURANT_NO_ANSWER);
        return order;
    }

}
