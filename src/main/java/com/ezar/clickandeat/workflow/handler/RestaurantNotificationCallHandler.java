package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_CALL_RESTAURANT;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.NOTIFICATION_STATUS_CALL_IN_PROGRESS;

@Component
public class RestaurantNotificationCallHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantNotificationCallHandler.class);

    @Autowired
    private NotificationService notificationService;
    
    private String timeZone;
    
    @Override
    public String getWorkflowAction() {
        return ACTION_CALL_RESTAURANT;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        Restaurant restaurant = order.getRestaurant();
        
        if( !restaurant.getNotificationOptions().isReceiveNotificationCall()) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("Restaurant " + restaurant.getName() + " is not set to accept calls, not placing call");
            }
            return order;
        }
                        
        DateTime now = new DateTime(DateTimeZone.forID(timeZone));
        String deliveryType = order.getDeliveryType();

        if( Order.DELIVERY.equals(deliveryType) && !restaurant.isOpenForDelivery(now)) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("Restaurant " + restaurant.getName() + " is not currently open for delivery not placing call");
            }
            return order;
        }

        if( Order.COLLECTION.equals(deliveryType) && !restaurant.isOpenForCollection(now)) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("Restaurant " + restaurant.getName() + " is not currently open for collection not placing call");
            }
            return order;
        }

        try {
            notificationService.placeOrderNotificationCallToRestaurant(order);
            order.addOrderUpdate("Placed order notification call to restaurant");
            order.setOrderNotificationCallCount(order.getOrderNotificationCallCount() + 1 );
            order.setLastCallPlacedTime(new DateTime(DateTimeZone.forID(timeZone)));
            order.setOrderNotificationStatus(NOTIFICATION_STATUS_CALL_IN_PROGRESS);
        }
        catch( Exception ex ) {
            LOGGER.error("Error placing order notification call to restaurant");
            throw new WorkflowException(ex);
        }

        return order;
    }


    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
