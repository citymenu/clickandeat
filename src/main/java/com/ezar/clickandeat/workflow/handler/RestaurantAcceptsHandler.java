package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.workflow.WorkflowException;
import com.ezar.clickandeat.workflow.WorkflowStatusException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Component
public class RestaurantAcceptsHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantAcceptsHandler.class);

    @Autowired
    private NotificationService notificationService;
    
    @Override
    public String getWorkflowAction() {
        return ACTION_RESTAURANT_ACCEPTS;
    }

    @Override
    public boolean isActionValidForOrder(Order order) {
        return ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus());
    }


    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        order.addOrderUpdate("Restaurant accepted order");

        Restaurant restaurant = order.getRestaurant();
        int deliveryTimeMinutes = restaurant.getDeliveryOptions().getDeliveryTimeMinutes() == null? 0: restaurant.getDeliveryOptions().getDeliveryTimeMinutes().intValue(); 

        // Update expected delivery time for the restaurant (if the user has not requested a specific time and date)
        if( Order.DELIVERY.equals(order.getDeliveryType()) && order.getExpectedDeliveryTime() == null ) {
            order.setExpectedDeliveryTime(new DateTime().plusMinutes(deliveryTimeMinutes));
        }
        
        try {
            notificationService.sendRestaurantAcceptedConfirmationToCustomer(order);
            order.addOrderUpdate("Sent confirmation of restaurant acceptance to customer");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending confirmation of restaurant acceptance to customer",ex);
            order.addOrderUpdate("Error sending confirmation of restaurant acceptance to customer: " + ex.getMessage());
        }

        order.setOrderStatus(ORDER_STATUS_RESTAURANT_ACCEPTED);
        return order;
    }

}
