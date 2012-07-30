package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.workflow.WorkflowException;
import com.ezar.clickandeat.workflow.WorkflowStatusException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Component
public class RestaurantAcceptedHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantAcceptedHandler.class);

    @Autowired
    private NotificationService notificationService;
    
    @Override
    public String getWorkflowAction() {
        return ACTION_RESTAURANT_ACCEPTED;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        if( !ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus())) {
            throw new WorkflowStatusException("Order should be in awaiting restaurant state");
        }

        order.addOrderUpdate("Restaurant accepted order");

        try {
            notificationService.sendRestaurantAcceptedConfirmationToCustomer(order);
            order.addOrderUpdate("Sent confirmation of restaurant acceptance to customer");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending confirmation of restaurant acceptance to customer",ex);
            throw new WorkflowException(ex);
        }

        order.setOrderStatus(ORDER_STATUS_RESTAURANT_ACCEPTED);
        return order;
    }

}
