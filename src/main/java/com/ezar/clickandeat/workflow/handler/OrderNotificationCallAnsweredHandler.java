package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_CALL_ANSWERED;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.NOTIFICATION_STATUS_RESTAURANT_ANSWERED;

@Component
public class OrderNotificationCallAnsweredHandler implements IWorkflowHandler {
    
    @Override
    public String getWorkflowAction() {
        return ACTION_CALL_ANSWERED;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Restaurant answered order notification call");
        order.setOrderNotificationStatus(NOTIFICATION_STATUS_RESTAURANT_ANSWERED);
        return order;
    }

}
