package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.workflow.WorkflowException;
import com.ezar.clickandeat.workflow.WorkflowStatusException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Notifying restaurant of order id: " + order.getOrderId());
        }

        try {
            notificationService.placeOrderNotificationCallToRestaurant(order);
            order.addOrderUpdate("Placed order notification call to restaurant");
            order.setOrderNotificationCallCount(order.getOrderNotificationCallCount() + 1 );
            order.setLastCallPlacedTime(new DateTime(DateTimeZone.forID(timeZone)));
            order.setOrderNotificationStatus(NOTIFICATION_CALL_STATUS_CALL_IN_PROGRESS);
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
