package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ACTION_SEND_CANCEL_OFFER_TO_CUSTOMER;

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
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

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
