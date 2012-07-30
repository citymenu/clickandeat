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
public class OrderPlacedHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(OrderPlacedHandler.class);

    @Autowired
    private NotificationService notificationService;
    
    @Override
    public String getWorkflowAction() {
        return ACTION_ORDER_PLACED;
    }

    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing placing of order id: " + order.getOrderId());
        }

        // Validate current status
        if( !ORDER_STATUS_BASKET.equals(order.getOrderStatus())) {
            throw new WorkflowStatusException("Order must be in 'BASKET' status");
        }
        
        // Send notifications to restaurant and customer
        try {
            notificationService.sendOrderNotificationToRestaurant(order);

            // Send notification email to customer
            notificationService.sendOrderConfirmationToCustomer(order);
        }
        catch( Exception ex ) {
            LOGGER.error("Error sending notifications to restaurant and customer");
            throw new WorkflowException(ex);
        }

        // Update Order status
        order.setOrderStatus(ORDER_STATUS_AWAITING_RESTAURANT);
        return order;
    }

}
