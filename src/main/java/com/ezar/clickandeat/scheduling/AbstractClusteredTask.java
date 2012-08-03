package com.ezar.clickandeat.scheduling;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

public abstract class AbstractClusteredTask implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(AbstractClusteredTask.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        cleanUp();
    }


    protected boolean shouldExecute() {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Checking if task name [" + getTaskName() + "] should run on this instance");
        }
        String uuid = UUID.randomUUID().toString();
        BoundValueOperations<String,String> valueOperations = redisTemplate.boundValueOps(getTaskName());
        return valueOperations.setIfAbsent(uuid);
    }


    protected void cleanUp() {
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaning up task: " + getTaskName());
        }
        redisTemplate.delete(getTaskName());
    }

    public abstract String getTaskName();


}
