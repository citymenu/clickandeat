package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Component
public class OrderNotificationCallNoAnswerHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(OrderNotificationCallNoAnswerHandler.class);
    
    private int maximumCallAttempts;
    
    @Override
    public String getWorkflowAction() {
        return ACTION_CALL_NOT_ANSWERED;
    }

    @Override
    public boolean isActionValidForOrder(Order order) {
        return true;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        order.addOrderUpdate("No answer for order notification call to restaurant");
        if( order.getOrderNotificationCallCount() >= maximumCallAttempts ) {
            if( LOGGER.isDebugEnabled()) {
                order.addOrderUpdate("Unable to contact restaurant " + order.getRestaurant().getName() + " after "
                        + order.getOrderNotificationCallCount() + " attempts, giving up");
            }
            order.addOrderUpdate("Unable to contact restaurant after " + order.getOrderNotificationCallCount() + " attempts");
            order.setOrderNotificationStatus(NOTIFICATION_STATUS_RESTAURANT_FAILED_TO_RESPOND);
        }
        else {
            order.setOrderNotificationStatus(NOTIFICATION_STATUS_RESTAURANT_NO_ANSWER);
        }
        return order;
    }


    @Required
    @Value(value="${twilio.maximumCallAttempts}")
    public void setMaximumCallAttempts(int maximumCallAttempts) {
        this.maximumCallAttempts = maximumCallAttempts;
    }
}
