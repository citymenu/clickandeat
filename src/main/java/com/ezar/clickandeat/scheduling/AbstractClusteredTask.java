package com.ezar.clickandeat.scheduling;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.UUID;

public abstract class AbstractClusteredTask extends QuartzJobBean {

    private static final Logger LOGGER = Logger.getLogger(AbstractClusteredTask.class);

    private StringRedisTemplate redisTemplate;

    private String taskName;

    public void executeInternal(JobExecutionContext context) {

        boolean shouldExecute = false;
        String uuid = UUID.randomUUID().toString();
        BoundValueOperations<String,String> valueOperations = redisTemplate.boundValueOps(taskName);

        try {
            shouldExecute = valueOperations.setIfAbsent(uuid);
            if( shouldExecute ) {
                LOGGER.info("Will execute scheduled task");
                doExecute(context);
            }
            else {
                LOGGER.info("Not executing scheduled task");
            }
        }
        finally {
            if( shouldExecute ) {
                redisTemplate.delete(taskName);
            }
        }
    }


    public abstract void doExecute(JobExecutionContext context);


    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
