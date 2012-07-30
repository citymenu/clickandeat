package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_NOTIFICATION_CALL_NO_ANSWER;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.NOTIFICATION_CALL_STATUS_RESTAURANT_NO_ANSWER;

@Component
public class OrderNotificationCallNoAnswerHandler implements IWorkflowHandler {
    
    @Override
    public String getWorkflowAction() {
        return ACTION_NOTIFICATION_CALL_NO_ANSWER;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("No answer for order notification call to restaurant");
        order.setOrderNotificationStatus(NOTIFICATION_CALL_STATUS_RESTAURANT_NO_ANSWER);
        return order;
    }

}
