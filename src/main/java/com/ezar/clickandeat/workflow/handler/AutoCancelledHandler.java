package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_AUTO_CANCELLED;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ORDER_STATUS_AUTO_CANCELLED;

@Component
public class AutoCancelledHandler implements IWorkflowHandler {

    private static final Logger LOGGER = Logger.getLogger(AutoCancelledHandler.class);

    @Autowired
    private NotificationService notificationService;

    @Override
    public String getWorkflowAction() {
        return ACTION_AUTO_CANCELLED;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        order.addOrderUpdate("System auto cancelled order due to no response from restaurant");

        try {
            notificationService.sendAutoCancelledConfirmationToCustomer(order);
            order.addOrderUpdate("Sent confirmation of auto cancelling order to customer");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending confirmation of auto cancelling order to customer",ex);
            throw new WorkflowException(ex);
        }

        try {
            notificationService.sendAutoCancelledConfirmationToRestaurant(order);
            order.addOrderUpdate("Sent confirmation of auto cancelling order to restaurant");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending confirmation of auto cancelling order to restaurant",ex);
            throw new WorkflowException(ex);
        }

        order.setOrderStatus(ORDER_STATUS_AUTO_CANCELLED);
        return order;
    }

}
