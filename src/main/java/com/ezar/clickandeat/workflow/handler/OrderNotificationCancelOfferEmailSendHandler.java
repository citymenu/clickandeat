package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_SEND_CANCEL_OFFER_TO_CUSTOMER;
import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ORDER_STATUS_AWAITING_RESTAURANT;

@Component
public class OrderNotificationCancelOfferEmailSendHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(OrderNotificationCancelOfferEmailSendHandler.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    public String getWorkflowAction() {
        return ACTION_SEND_CANCEL_OFFER_TO_CUSTOMER;
    }


    @Override
    public boolean isActionValidForOrder(Order order) {
        return ORDER_STATUS_AWAITING_RESTAURANT.equals(order.getOrderStatus());
    }


    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending cancellation offer to customer for order id: " + order.getOrderId());
        }

        try {
            notificationService.sendOrderCancellationOfferToCustomer(order);
            order.setCancellationOfferEmailSent(true);
            order.addOrderUpdate("Sent offer to cancel order to customer");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending offer to cancel order to customer",ex);
            throw new WorkflowException(ex);
        }
        
        return order;
    }

}
