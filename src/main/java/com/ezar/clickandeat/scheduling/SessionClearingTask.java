package com.ezar.clickandeat.scheduling;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.data.mongodb.core.MongoOperations;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class SessionClearingTask extends AbstractClusteredTask {

    private static final Logger LOGGER = Logger.getLogger(SessionClearingTask.class);
    
    private MongoOperations mongoOperations;

    @Override
    public void doExecute(JobExecutionContext context) {
        LOGGER.info("Clearing expired sessions from database");
        mongoOperations.remove(query(where("valid").is(false)), "sessions");
    }

    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}
