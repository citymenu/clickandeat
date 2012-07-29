package com.ezar.clickandeat.scheduling;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.UUID;

public abstract class AbstractClusteredTask implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(AbstractClusteredTask.class);

    private StringRedisTemplate redisTemplate;

    private String taskName;


    @Override
    public void afterPropertiesSet() throws Exception {
        cleanUp();
    }


    protected boolean shouldExecute() {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Checking if task name [" + taskName + "] should run on this instance");
        }
        String uuid = UUID.randomUUID().toString();
        BoundValueOperations<String,String> valueOperations = redisTemplate.boundValueOps(taskName);
        return valueOperations.setIfAbsent(uuid);
    }


    protected void cleanUp() {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaning up redis for task name [" + taskName + "]");
        }
        redisTemplate.delete(taskName);
    }



    @Required
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Required
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
