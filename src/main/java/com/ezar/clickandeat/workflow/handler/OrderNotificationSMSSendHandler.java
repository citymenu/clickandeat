package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_NOTIFICATION_SEND_SMS;

@Component
public class OrderNotificationSMSSendHandler implements IWorkflowHandler {
    
    @Override
    public String getWorkflowAction() {
        return ACTION_NOTIFICATION_SEND_SMS;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Sent notification SMS to restaurant");
        return order;
    }

}
