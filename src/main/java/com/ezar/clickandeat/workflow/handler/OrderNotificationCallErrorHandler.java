package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_NOTIFICATION_CALL_ERROR;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.NOTIFICATION_CALL_STATUS_ERROR;

@Component
public class OrderNotificationCallErrorHandler implements IWorkflowHandler {
    
    @Override
    public String getWorkflowAction() {
        return ACTION_NOTIFICATION_CALL_ERROR;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Error occurred placing notification call to restaurant");
        order.setOrderNotificationStatus(NOTIFICATION_CALL_STATUS_ERROR);
        return order;
    }

}
