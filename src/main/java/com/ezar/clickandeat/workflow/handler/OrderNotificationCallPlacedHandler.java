package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_NOTIFICATION_CALL_PLACED;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.NOTIFICATION_CALL_STATUS_CALL_IN_PROGRESS;

@Component
public class OrderNotificationCallPlacedHandler implements IWorkflowHandler {
    
    @Override
    public String getWorkflowAction() {
        return ACTION_NOTIFICATION_CALL_PLACED;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Placed order notification call to restaurant");
        order.setOrderNotificationCallCount(order.getOrderNotificationCallCount() + 1 );
        order.setOrderNotificationStatus(NOTIFICATION_CALL_STATUS_CALL_IN_PROGRESS);
        return order;
    }

}
