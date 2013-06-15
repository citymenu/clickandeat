package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.util.MapUtil;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

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
    public boolean isActionValidForOrder(Order order) {
        return ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus());
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
                   
        boolean ignoreOpen = MapUtil.getBooleanMapValue(context,"ignoreOpen");
        if( !ignoreOpen && !restaurant.isOpen(new DateTime())) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("Restaurant " + restaurant.getName() + " is not currently open, not placing call");
            }
            return order;
        }

        try {
            notificationService.placeOrderNotificationCallToRestaurant(order);
            order.setCallInProgress(true);
            order.addOrderUpdate("Placed order notification call to restaurant");
            order.setOrderNotificationCallCount(order.getOrderNotificationCallCount() + 1 );
            order.setLastCallPlacedTime(new DateTime());
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
