package com.ezar.clickandeat.scheduling;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

public abstract class AbstractClusteredTask {

    private static final Logger LOGGER = Logger.getLogger(AbstractClusteredTask.class);

    private StringRedisTemplate redisTemplate;

    private String taskName;
    
    public void execute() {

        boolean shouldExecute = false;
        String uuid = UUID.randomUUID().toString();
        BoundValueOperations<String,String> valueOperations = redisTemplate.boundValueOps(taskName);

        try {
            shouldExecute = valueOperations.setIfAbsent(uuid);
            if( shouldExecute ) {
                executeInternal();
            }
        }
        finally {
            if( shouldExecute ) {
                redisTemplate.delete(taskName);
            }
        }
    }


    public abstract void executeInternal();


    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
