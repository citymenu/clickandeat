package com.ezar.clickandeat.scheduling;


import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class OpenOrderProcessingTask extends AbstractClusteredTask {

    private static final Logger LOGGER = Logger.getLogger(SessionClearingTask.class);

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;
    
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

            List<Order> orders = mongoOperations.find(new Query(where("orderStatus").is(ORDER_STATUS_AWAITING_RESTAURANT)),Order.class);
            LOGGER.info("Found " + orders.size() + " orders with status 'AWAITING_RESTAURANT'");

            DateTime now = new DateTime(DateTimeZone.forID(timeZone));
            
            for(Order order: orders ) {

                DateTime orderPlacedTime = order.getOrderPlacedTime();
                if(orderPlacedTime.plusMinutes(minutesBeforeAutoCancelOrder).isAfter(new DateTime(DateTimeZone.forID(timeZone)))) {
                    orderWorkflowEngine.processAction(order,ACTION_AUTO_CANCELLED);
                }

                
            }
            
            // If the order has been placed before the autocancel threshold, auto-cancel the order
            
            
            
            
            
            // Iterate through orders and attempt to call again if enough time has elapsed since the last try
            for( Order order: orders ) {
                DateTime lastCallTime = order.getLastCallPlacedTime();
                if(lastCallTime.plusSeconds(secondsBeforeRetryCall).isBefore(now)) {
                    LOGGER.info("Retrying order notification call for order id: " + order.getOrderId());
                    try {
                        orderWorkflowEngine.processAction(order,ACTION_CALL_RESTAURANT);
                    }
                    catch( Exception ex ) {
                        LOGGER.error("Error occurred placing order call to restaurant for order id: " + order.getOrderId());
                        orderWorkflowEngine.processAction(order,ACTION_NOTIFICATION_CALL_ERROR);
                    }
                }
            }
            
             
            
        }
        catch (Exception ex) {
            LOGGER.error("Error retrying call notifications",ex);
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
