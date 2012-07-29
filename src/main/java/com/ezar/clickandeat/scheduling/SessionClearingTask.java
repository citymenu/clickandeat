package com.ezar.clickandeat.scheduling;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class SessionClearingTask extends AbstractClusteredTask {

    private static final Logger LOGGER = Logger.getLogger(SessionClearingTask.class);

    @Autowired
    private MongoOperations mongoOperations;

    private int purgeIntervalHours;

    @Scheduled(cron="0 0 14 * * ?")
    public void execute() {
        
        boolean shouldRun = false;
        
        try {
            shouldRun = shouldExecute();
            if( !shouldRun ) {
                LOGGER.info("Not running clustered task");
                return;
            }
            final long now = System.currentTimeMillis();
            final long expiry = purgeIntervalHours * 60 * 60 * 1000l;

            // Get all order ids from expired sessions
            Set<String> orderIds = mongoOperations.execute("sessions", new CollectionCallback<Set<String>>() {
                @Override
                public Set<String> doInCollection(DBCollection collection) throws MongoException, DataAccessException {
                    Set<String> orderIds = new HashSet<String> ();
                    BasicDBObject query = new BasicDBObject();
                    query.put("accessed", new BasicDBObject("$lt",(now - expiry)));
                    DBCursor cursor = collection.find(query);
                    while(cursor.hasNext()) {
                        DBObject result = cursor.next();
                        String orderId = (String)result.get("orderid");
                        if( orderId != null) {
                            orderIds.add(orderId);
                        }
                    }
                    return orderIds;
                }
            });

            // Delete expired orders where the status is 'BASKET'
            if( orderIds.size() > 0 ) {
                LOGGER.info("Checking for unfulfilled orders from " + orderIds.size() + " expired sessions");
                mongoOperations.remove(query(where("orderId").in(orderIds).and("orderStatus").is(OrderWorkflowEngine.ORDER_STATUS_BASKET)), Order.class);
            }

            // Delete expired sessions
            mongoOperations.remove(query(where("accessed").lt(now - expiry)), "sessions");
            LOGGER.info("Finished removing expired sessions");

        }
        catch (Exception ex) {
            LOGGER.error("Error clearing expired sessions",ex);
        }
        finally {
            if( shouldRun ) {
                cleanUp();
            }
        }
    }

    @Required
    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Required
    public void setPurgeIntervalHours(int purgeIntervalHours) {
        this.purgeIntervalHours = purgeIntervalHours;
    }
}
