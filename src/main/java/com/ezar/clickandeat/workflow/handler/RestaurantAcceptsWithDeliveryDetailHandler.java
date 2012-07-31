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
public class RestaurantAcceptsWithDeliveryDetailHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantAcceptsWithDeliveryDetailHandler.class);

    @Autowired
    private NotificationService notificationService;

    @Override
    public String getWorkflowAction() {
        return ACTION_RESTAURANT_ACCEPTS_WITH_DELIVERY_DETAIL;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        if( !ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus())) {
            throw new WorkflowStatusException(order,"Order should be in awaiting restaurant state");
        }

        String deliveryMinutes = (String)context.get("DeliveryMinutes");
        order.addOrderUpdate("Restaurant accepted order with modified delivery time of " + deliveryMinutes + " minutes");

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
