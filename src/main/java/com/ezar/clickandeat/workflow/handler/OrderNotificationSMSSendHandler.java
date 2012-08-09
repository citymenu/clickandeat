package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_SEND_SMS;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ORDER_STATUS_AWAITING_RESTAURANT;

@Component
public class OrderNotificationSMSSendHandler implements IWorkflowHandler {
    
    @Override
    public String getWorkflowAction() {
        return ACTION_SEND_SMS;
    }


    @Override
    public boolean isActionValidForOrder(Order order) {
        return ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus());
    }


    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("Sent notification SMS to restaurant");
        return order;
    }

}
