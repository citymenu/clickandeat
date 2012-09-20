package com.ezar.clickandeat.scheduling;

import com.ezar.clickandeat.model.Order;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ORDER_STATUS_BASKET;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class ExpiredOrderCleanupTask extends AbstractClusteredTask {

    private static final Logger LOGGER = Logger.getLogger(ExpiredOrderCleanupTask.class);

    @Autowired
    private MongoOperations operations;


    @Scheduled(cron="0 11 23 * * ?")
    public void execute() {

        boolean shouldRun = false;

        try {
            shouldRun = shouldExecute();
            if( !shouldRun ) {
                LOGGER.info("Not running clustered task");
                return;
            }

            LOGGER.info("Checking for any orders with status 'BASKET' which are over 2 days old");
            DateTime cutoff = new DateTime().minusDays(2);
            List<Order> orders = operations.find(new Query(where("orderStatus").is(ORDER_STATUS_BASKET)), Order.class);
            LOGGER.info("Found " + orders.size() + " orders with status 'BASKET'");

            // Get all expired orders
            Set<String> orderIds = new HashSet<String>();
            for(Order order: orders ) {
                DateTime orderCreatedTime = order.getOrderCreatedTime();
                if( orderCreatedTime.isBefore(cutoff)) {
                    orderIds.add(order.getOrderId());
                    if( LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Order id: " + order.getOrderId() + " has expired, will remove from database");
                    }
                }
            }
            
            // Delete expired orders
            if( orderIds.size() > 0 ) {
                LOGGER.info("Deleting " + orderIds.size() + " expired orders");
                operations.remove(new Query(where("orderId").in(orderIds)), Order.class);
            }
        }
        catch (Exception ex) {
            LOGGER.error("Error occurred removing expired orders orders: " + ex.getMessage(),ex);
        }
        finally {
            cleanUp();
        }
    }
    

    @Override
    public String getTaskName() {
        return "expiredOrderCleanupTask";
    }

    
    
}
