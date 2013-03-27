package com.ezar.clickandeat.scheduling;

import com.ezar.clickandeat.model.Order;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ORDER_STATUS_BASKET;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class ExpiredOrderCleanupTask implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(ExpiredOrderCleanupTask.class);

    @Autowired
    private MongoOperations operations;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private DistributedLock lock;

    
    @Override
    public void afterPropertiesSet() throws Exception {
        this.lock = new DistributedLock(redisTemplate, getClass().getSimpleName());
    }


    @Scheduled(cron="0 0 0/4 * * ?")
    public void execute() {

        try {
            if(lock.acquire()) {

                LOGGER.info("Checking for any orders with status 'BASKET' which contain no itema and are over 2 days old");
                DateTime cutoff = new DateTime().minusDays(2);
                List<Order> orders = operations.find(new Query(where("orderStatus").is(ORDER_STATUS_BASKET)), Order.class);
                LOGGER.info("Found " + orders.size() + " orders with status 'BASKET'");
    
                // Get all expired orders
                for(Order order: orders ) {
                    DateTime orderCreatedTime = order.getOrderCreatedTime();
                    if( orderCreatedTime.isBefore(cutoff) && order.getOrderItems().size() == 0) {
                        LOGGER.info("Order id: " + order.getOrderId() + " has expired, deleting from database");
                        operations.remove(query(where("orderId").is(order.getOrderId())), Order.class);
                    }
                }
                
            }
        }
        catch (Exception ex) {
            LOGGER.error("Error occurred removing expired orders orders: " + ex.getMessage(),ex);
        }
        finally {
            lock.release();
        }
    }


}
