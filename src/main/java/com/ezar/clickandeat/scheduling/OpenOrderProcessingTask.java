package com.ezar.clickandeat.scheduling;


import com.ezar.clickandeat.exception.ExceptionHandler;
import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Component
public class OpenOrderProcessingTask extends AbstractClusteredTask {

    private static final Logger LOGGER = Logger.getLogger(SessionClearingTask.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;
    
    @Autowired
    private ExceptionHandler exceptionHandler;
    
    private int secondsBeforeRetryCall;
    
    private int minutesBeforeSendCancellationEmail;
    
    private int minutesBeforeAutoCancelOrder;
    
    private String timeZone;


    @Scheduled(cron="0 0/1 * * * ?")
    public void execute() {

        boolean shouldRun = false;

        try {
            shouldRun = shouldExecute();
            if( !shouldRun ) {
                LOGGER.info("Not running clustered task");
                return;
            }

            LOGGER.info("Checking for any orders with status 'AWAITING_RESTAURANT'");

            List<Order> orders = orderRepository.findByOrderStatus(ORDER_STATUS_AWAITING_RESTAURANT);
            LOGGER.info("Found " + orders.size() + " orders with status 'AWAITING_RESTAURANT'");

            for(Order order: orders ) {

                DateTime orderPlacedTime = order.getOrderPlacedTime();
                
                // Auto cancel orders which have been awaiting confirmation for too long
                if(orderPlacedTime.isBefore(new DateTime(DateTimeZone.forID(timeZone)).minusMinutes(minutesBeforeAutoCancelOrder))) {
                    try {
                        LOGGER.info("Order id: " + order.getOrderId() + " has been awaiting confirmation for more than " + minutesBeforeAutoCancelOrder + " minutes, auto-cancelling");
                        orderWorkflowEngine.processAction(order, ACTION_AUTO_CANCEL);
                    }
                    catch( Exception ex ) {
                        exceptionHandler.handleException(ex);
                    }
                }

                // Send email to customer giving them the option to cancel the order if it has been awaiting confirmation for too long
                else if(!order.getCancellationOfferEmailSent() && orderPlacedTime.isBefore(new DateTime(DateTimeZone.forID(timeZone)).minusMinutes(minutesBeforeSendCancellationEmail))) {
                    try {
                        LOGGER.info("Order id: " + order.getOrderId() + " has been awaiting confirmation for more than " + minutesBeforeSendCancellationEmail + " minutes, sending email");
                        orderWorkflowEngine.processAction(order, ACTION_SEND_CANCEL_OFFER_TO_CUSTOMER);
                    }
                    catch( Exception ex ) {
                        exceptionHandler.handleException(ex);
                    }
                }

                // Attempt to call restaurant again
                else if(!NOTIFICATION_STATUS_RESTAURANT_FAILED_TO_RESPOND.equals(order.getOrderNotificationStatus())) {
                    NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();
                    DateTime lastCallTime = order.getLastCallPlacedTime();
                    if(notificationOptions.isReceiveNotificationCall()) {
                        if(lastCallTime == null || lastCallTime.isBefore(new DateTime(DateTimeZone.forID(timeZone)).minusSeconds(secondsBeforeRetryCall))) {
                            LOGGER.info("Retrying order notification call for order id: " + order.getOrderId());
                            try {
                                orderWorkflowEngine.processAction(order,ACTION_CALL_RESTAURANT);
                            }
                            catch( Exception ex ) {
                                LOGGER.error("Error occurred placing order call to restaurant for order id: " + order.getOrderId());
                                orderWorkflowEngine.processAction(order, ACTION_CALL_ERROR);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error("Error occurred processing open orders: " + ex.getMessage(),ex);
        }
        finally {
            if( shouldRun ) {
                cleanUp();
            }
        }
    }

    @Override
    public String getTaskName() {
        return "openOrderProcessingTask";
    }

    @Required
    @Value(value="${twilio.secondsBeforeRetryCall}")
    public void setSecondsBeforeRetryCall(int secondsBeforeRetryCall) {
        this.secondsBeforeRetryCall = secondsBeforeRetryCall;
    }

    @Required
    @Value(value="${twilio.minutesBeforeSendCancellationEmail}")
    public void setMinutesBeforeSendCancellationEmail(int minutesBeforeSendCancellationEmail) {
        this.minutesBeforeSendCancellationEmail = minutesBeforeSendCancellationEmail;
    }

    @Required
    @Value(value="${twilio.minutesBeforeAutoCancelOrder}")
    public void setMinutesBeforeAutoCancelOrder(int minutesBeforeAutoCancelOrder) {
        this.minutesBeforeAutoCancelOrder = minutesBeforeAutoCancelOrder;
    }

    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    
}
