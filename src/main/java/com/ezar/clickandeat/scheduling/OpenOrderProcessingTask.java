package com.ezar.clickandeat.scheduling;


import com.ezar.clickandeat.exception.ExceptionHandler;
import com.ezar.clickandeat.model.NotificationOptions;
import com.ezar.clickandeat.model.OpeningTime;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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
                LOGGER.info("Order id: " + order.getOrderId() + " was placed at: " + orderPlacedTime);

                Restaurant restaurant = order.getRestaurant();
                DateTime now = new DateTime(DateTimeZone.forID(timeZone));
                LOGGER.info("Current time is: " + now);
                
                // Get the time the restaurant opened
                DateTime restaurantOpenedTime = Order.DELIVERY.equals(order.getDeliveryType())? restaurant.getDeliveryOpeningTime(now): restaurant.getCollectionOpeningTime(now);

                // Don't do anything if the restaurant has not opened yet today
                LOGGER.info("Restaurant " + restaurant.getName() + " opened time today is: " + restaurantOpenedTime);
                if( restaurantOpenedTime.isAfter(now)) {
                    LOGGER.info("Restaurant " + restaurant.getName() + " has not opened yet, not doing any processing");
                    continue;
                }

                // Auto cancel orders which have been awaiting confirmation for too long and the restaurant has been open long enough to respond
                DateTime autoCancelCutoff = new DateTime(DateTimeZone.forID(timeZone)).minusMinutes(minutesBeforeAutoCancelOrder);
                LOGGER.info("Auo cancel cutoff time is: " + autoCancelCutoff);
                if(orderPlacedTime.isBefore(autoCancelCutoff) && restaurantOpenedTime.isBefore(autoCancelCutoff)) {
                    try {
                        LOGGER.info("Order id: " + order.getOrderId() + " has been awaiting confirmation for more than " + minutesBeforeAutoCancelOrder + " minutes, auto-cancelling");
                        orderWorkflowEngine.processAction(order, ACTION_AUTO_CANCEL);
                    }
                    catch( Exception ex ) {
                        exceptionHandler.handleException(ex);
                    }
                    continue;
                }

                // Send email to customer giving them the option to cancel the order if it has been awaiting confirmation for too long
                DateTime cancellationOfferCutoff = new DateTime(DateTimeZone.forID(timeZone)).minusMinutes(minutesBeforeSendCancellationEmail);
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

                // Do nothing if the restaurant is not open to receive calls
                if( Order.DELIVERY.equals(order.getDeliveryType()) && !restaurant.isOpenForDelivery(now)) {
                    LOGGER.info("Restaurant not currently open for delivery orders, not calling");
                    continue;
                }

                if( Order.COLLECTION.equals(order.getDeliveryType()) && !restaurant.isOpenForCollection(now)) {
                    LOGGER.info("Restaurant not currently open for collection orders, not calling");
                    continue;
                }

                // Attempt to call restaurant again if they are open to receive calls
                if(!NOTIFICATION_STATUS_RESTAURANT_FAILED_TO_RESPOND.equals(order.getOrderNotificationStatus())) {
                    NotificationOptions notificationOptions = order.getRestaurant().getNotificationOptions();
                    DateTime lastCallTime = order.getLastCallPlacedTime();
                    DateTime lastCallCutoff = new DateTime(DateTimeZone.forID(timeZone)).minusSeconds(secondsBeforeRetryCall);
                    if(notificationOptions.isReceiveNotificationCall()) {
                        if(lastCallTime == null || lastCallTime.isBefore(lastCallCutoff)) {
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
