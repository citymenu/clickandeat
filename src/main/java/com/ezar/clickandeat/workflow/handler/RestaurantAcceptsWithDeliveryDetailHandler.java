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
public class RestaurantAcceptsWithDeliveryDetailHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantAcceptsWithDeliveryDetailHandler.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public String getWorkflowAction() {
        return ACTION_RESTAURANT_ACCEPTS_WITH_DELIVERY_DETAIL;
    }

    @Override
    public boolean isActionValidForOrder(Order order) {
        return ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus());
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        Integer deliveryMinutes = (Integer)context.get("DeliveryMinutes");
        order.addOrderUpdate("Restaurant accepted order with modified delivery/collection time of " + deliveryMinutes + " minutes");
        Restaurant restaurant = order.getRestaurant();
        order.setRestaurantActionedTime(new DateTime());

        // Update the last time the restaurant responded to the system
        restaurant.setLastOrderReponseTime(new DateTime());
        restaurantRepository.saveRestaurant(restaurant);

        // Update expected order delivery time (if the order is for delivery)
        if( Order.DELIVERY.equals(order.getDeliveryType())) {
            order.setDeliveryTimeNonStandard(true);
            if( order.getExpectedDeliveryTime() == null ) {
                order.setRestaurantConfirmedTime(new DateTime().plusMinutes(deliveryMinutes ));
            }
            else {
                order.setRestaurantConfirmedTime(order.getExpectedDeliveryTime().plusMinutes(deliveryMinutes));
            }
        }

        // Update expected order collection time (if the order is for delivery)
        if( Order.COLLECTION.equals(order.getDeliveryType())) {
            order.setDeliveryTimeNonStandard(true);
            if( order.getExpectedCollectionTime() == null ) {
                order.setRestaurantConfirmedTime(new DateTime().plusMinutes(restaurant.getDeliveryTimeMinutes() + deliveryMinutes ));
            }
            else {
                order.setRestaurantConfirmedTime(order.getExpectedCollectionTime().plusMinutes(deliveryMinutes));
            }
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
