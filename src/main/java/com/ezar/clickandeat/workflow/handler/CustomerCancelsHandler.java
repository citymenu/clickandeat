package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.workflow.WorkflowException;
import com.ezar.clickandeat.workflow.WorkflowStatusException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Component
public class CustomerCancelsHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(CustomerCancelsHandler.class);

    @Autowired
    private NotificationService notificationService;

    @Override
    public String getWorkflowAction() {
        return ACTION_CUSTOMER_CANCELS;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        if( !ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus())) {
            throw new WorkflowStatusException(order,"Order should be in awaiting restaurant state");
        }

        order.addOrderUpdate("Customer cancelled order");

        try {
            notificationService.sendCustomerCancelledConfirmationToRestaurant(order);
            order.addOrderUpdate("Sent confirmation of customer cancelling order to restaurant");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending confirmation of customer cancelling order to restaurant",ex);
            throw new WorkflowException(ex);
        }

        try {
            notificationService.sendCustomerCancelledConfirmationToCustomer(order);
            order.addOrderUpdate("Sent confirmation of customer cancelling order to customer");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending confirmation of customer cancelling order to customer",ex);
            throw new WorkflowException(ex);
        }

        order.setOrderStatus(ORDER_STATUS_CUSTOMER_CANCELLED);
        return order;
    }

}
