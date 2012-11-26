package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.payment.PaymentService;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.repository.VoucherRepository;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Component
public class SystemCancelsHandler implements IWorkflowHandler {
    
    private static final Logger LOGGER = Logger.getLogger(SystemCancelsHandler.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VoucherRepository voucherRepository;

    /**
     * List of status values that this handler is invalid for
     */
    private final List<String> invalidStatusList = Arrays.asList(
            ORDER_STATUS_BASKET,
            ORDER_STATUS_CUSTOMER_CANCELLED,
            ORDER_STATUS_SYSTEM_CANCELLED,
            ORDER_STATUS_RESTAURANT_DECLINED,
            ORDER_STATUS_AUTO_CANCELLED
    );

    @Override
    public String getWorkflowAction() {
        return ACTION_SYSTEM_CANCELS;
    }


    @Override
    public boolean isActionValidForOrder(Order order) {
        return !invalidStatusList.contains(order.getOrderStatus());
    }


    @Override
    public Order handle(Order order, Map<String, Object> context) throws WorkflowException {

        order.addOrderUpdate("Order cancelled by system operator");

        // Set any voucher on this order to be unused
        voucherRepository.markVoucherUnused(order.getVoucherId());

        // Update the last time the restaurant responded to the system
        Restaurant restaurant = order.getRestaurant();
        restaurant.setLastOrderReponseTime(new DateTime());
        restaurantRepository.saveRestaurant(restaurant);

        try {
            if( Order.PAYMENT_PRE_AUTHORISED.equals(order.getTransactionStatus()) || Order.PAYMENT_CAPTURED.equals(order.getTransactionStatus())) {
                //If the order is a test order we don't call the payment gateway
                if(order.getTestOrder()){
                    // If it is a test order only enter an Order update
                    order.addOrderUpdate("Test order. No real refund of credit card payment");
                }else{
                    String transactionType = order.getTransactionStatus().equals(Order.PAYMENT_PRE_AUTHORISED)? PaymentService.REVERSE: PaymentService.REFUND;
                    paymentService.processTransactionRequest(order,transactionType);
                    order.addOrderUpdate("Refunded customer credit card");
                }
                order.setTransactionStatus(Order.PAYMENT_REFUNDED);
            }
        }
        catch( Exception ex ) {
            LOGGER.error("Error processing refund of order",ex);
            order.addOrderUpdate("Error processing refund of order: " + ex.getMessage());
        }

        try {
            notificationService.sendSystemCancelledConfirmationToCustomer(order);
            order.addOrderUpdate("Sent confirmation of system operator cancelling order to customer");
        }
        catch (Exception ex ) {
            LOGGER.error("Error sending confirmation of system operator cancelling order to customer",ex);
            order.addOrderUpdate("Error sending confirmation of system operator cancelling order to customer: " + ex.getMessage());
        }

        order.setOrderStatus(ORDER_STATUS_SYSTEM_CANCELLED);
        return order;
    }

}
