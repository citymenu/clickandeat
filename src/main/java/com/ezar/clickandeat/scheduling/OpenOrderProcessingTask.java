package com.ezar.clickandeat.scheduling;


import com.ezar.clickandeat.exception.ExceptionHandler;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.ezar.clickandeat.workflow.WorkflowException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

@Component
public class OpenOrderProcessingTask implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(OpenOrderProcessingTask.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ExceptionHandler exceptionHandler;
    
    private int secondsBeforeRetryCall;

    private int secondsBeforeRetryAnsweredCall;
    
    private int minutesBeforeSendCancellationEmail;

    private int minutesBeforeAutoCancelOrder;

    private DistributedLock lock;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.lock = new DistributedLock(redisTemplate, getClass().getSimpleName());
    }
    

    @Scheduled(cron="0 0/1 * * * ?")
    public void execute() {

        try {
            if(lock.acquire()) {            

                LOGGER.info("Checking for any orders with status 'AWAITING_RESTAURANT'");
    
                List<Order> orders = orderRepository.findByOrderStatus(ORDER_STATUS_AWAITING_RESTAURANT);
                LOGGER.info("Found " + orders.size() + " orders with status 'AWAITING_RESTAURANT'");
    
                for(Order order: orders ) {
    
                    DateTime orderPlacedTime = order.getOrderPlacedTime();
                    String orderId = order.getOrderId();
                    LOGGER.info("Order id: " + orderId + " was placed at: " + orderPlacedTime);
    
                    Restaurant restaurant = order.getRestaurant();
                    DateTime now = new DateTime();
                    LOGGER.info("Current time is: " + now);

                    String notificationStatus = order.getOrderNotificationStatus();
                    LOGGER.info("Order id: " + orderId + " notification status is: " + notificationStatus);

                    // Get the time the restaurant opened
                    DateTime restaurantOpenedTime = restaurant.getEarlyOpeningTime(now);
    
                    // Don't do anything if the restaurant is not currently open
                    LOGGER.info("Restaurant " + restaurant.getName() + " opened time today is: " + restaurantOpenedTime);
                    if( restaurantOpenedTime == null || restaurantOpenedTime.isAfter(now)) {
                        LOGGER.info("Restaurant " + restaurant.getName() + " has not opened yet, not doing any processing");
                        continue;
                    }
    
                    // Auto cancel orders which have been awaiting confirmation for too long and the restaurant has been open long enough to respond
                    DateTime autoCancelCutoff = new DateTime().minusMinutes(minutesBeforeAutoCancelOrder);
                    LOGGER.info("Auo cancel cutoff time is: " + autoCancelCutoff);
                    if(orderPlacedTime.isBefore(autoCancelCutoff) && restaurantOpenedTime.isBefore(autoCancelCutoff)) {
                        try {
                            LOGGER.info("Order id: " + orderId + " has been awaiting confirmation for more than " + minutesBeforeAutoCancelOrder + " minutes, auto-cancelling");
                            orderWorkflowEngine.processAction(order, ACTION_AUTO_CANCEL);
                        }
                        catch( WorkflowException e ) {
                            LOGGER.error("Exception sending auto cancel email for orderId: " + order.getOrderId(),e);
                            order.setOrderStatus(ORDER_STATUS_AUTO_CANCELLED);
                            orderRepository.saveOrder(order);
                        }
                        catch( Exception ex ) {
                            exceptionHandler.handleException(ex);
                        }
                        continue;
                    }

                    // Send email to customer giving them the option to cancel the order if it has been awaiting confirmation for too long
                    DateTime cancellationOfferCutoff = new DateTime().minusMinutes(minutesBeforeSendCancellationEmail);
                    LOGGER.info("Cancellation offer cutoff time is: " + cancellationOfferCutoff);
                    if(!order.getCancellationOfferEmailSent() && orderPlacedTime.isBefore(cancellationOfferCutoff) && restaurantOpenedTime.isBefore(cancellationOfferCutoff)) {
                        try {
                            LOGGER.info("Order id: " + order.getOrderId() + " has been awaiting confirmation for more than " + minutesBeforeSendCancellationEmail + " minutes, sending email");
                            orderWorkflowEngine.processAction(order, ACTION_SEND_CANCEL_OFFER_TO_CUSTOMER);
                        }
                        catch( Exception ex ) {
                            exceptionHandler.handleException(ex);
                        }
                    }
    
                    if( NOTIFICATION_STATUS_CALL_IN_PROGRESS.equals(notificationStatus) || NOTIFICATION_STATUS_RESTAURANT_FAILED_TO_RESPOND.equals(notificationStatus)) {
                        LOGGER.info("Not attempting another call for order id:" + orderId + " as notification status is; " + notificationStatus);
                        continue;
                    }
                    
                    // If call was answered but order not accepted/rejected, retry after 5 minutes otherwise after 1 minute
                    DateTime lastCallCutoff = new DateTime().minusSeconds(NOTIFICATION_STATUS_RESTAURANT_ANSWERED.equals(notificationStatus)?
                            secondsBeforeRetryAnsweredCall: secondsBeforeRetryCall);
                    DateTime lastCallTime = order.getLastCallPlacedTime();
                    if(lastCallTime == null || lastCallTime.isBefore(lastCallCutoff)) {
                        try {
                            orderWorkflowEngine.processAction(order,ACTION_CALL_RESTAURANT);
                        }
                        catch( Exception ex ) {
                            LOGGER.error("Error occurred placing order call to restaurant for order id: " + order.getOrderId());
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error("Error occurred processing open orders: " + ex.getMessage(),ex);
        }
        finally {
            lock.release();
        }
    }


    @Required
    @Value(value="${twilio.secondsBeforeRetryCall}")
    public void setSecondsBeforeRetryCall(int secondsBeforeRetryCall) {
        this.secondsBeforeRetryCall = secondsBeforeRetryCall;
    }

    @Required
    @Value(value="${twilio.secondsBeforeRetryAnsweredCall}")
    public void setSecondsBeforeRetryAnsweredCall(int secondsBeforeRetryAnsweredCall) {
        this.secondsBeforeRetryAnsweredCall = secondsBeforeRetryAnsweredCall;
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

}
