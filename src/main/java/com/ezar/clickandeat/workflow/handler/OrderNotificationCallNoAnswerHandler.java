package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderNotificationCallNoAnswerHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(OrderNotificationCallNoAnswerHandler.class);

    @Override
    public String getWorkflowAction() {
        return OrderWorkflowEngine.ACTION_NOTIFICATION_CALL_NO_ANSWER;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("No answer for order notification call to restaurant");
        order.setOrderNotificationStatus(OrderWorkflowEngine.NOTIFICATION_CALL_STATUS_RESTAURANT_NO_ANSWER);
        return order;
    }

}
