package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.workflow.WorkflowException;
import com.ezar.clickandeat.workflow.WorkflowStatusException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Component
public class RestaurantDeclinesHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantDeclinesHandler.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RestaurantRepository restaurantRepository;


    @Override
    public String getWorkflowAction() {
        return ACTION_RESTAURANT_DECLINES;
    }


    @Override
    public boolean isActionValidForOrder(Order order) {
        return ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus());
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        order.addOrderUpdate("Restaurant declined order");
        order.setRestaurantActionedTime(new DateTime());

        // Update the last time the restaurant responded to the system
        Restaurant restaurant = order.getRestaurant();
        restaurant.setLastOrderReponseTime(new DateTime());
        restaurantRepository.saveRestaurant(restaurant);


        try {
            notificationService.sendRestaurantDeclinedConfirmationToCustomer(order);
            order.addOrderUpdate("Sent confirmation of restaurant declining order to customer");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending confirmation of restaurant declining order to customer",ex);
            order.addOrderUpdate("Error sending confirmation of restaurant declining order to customer: " + ex.getMessage());
        }

        order.setOrderStatus(ORDER_STATUS_RESTAURANT_DECLINED);
        return order;
    }

}
