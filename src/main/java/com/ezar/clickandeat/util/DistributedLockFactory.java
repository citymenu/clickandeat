package com.ezar.clickandeat.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DistributedLockFactory {

    private static final Logger LOGGER = Logger.getLogger(DistributedLockFactory.class);
    
    private static final int DEFAULT_EXPIRES_MS = 1000 * 60 * 5;
    
    private final Map<String,DistributedLock> locks = new HashMap<String,DistributedLock>();

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * @param id
     * @return
     */
    
    public boolean acquire(String id) {
        try {
            DistributedLock lock = locks.get(id);
            if( lock == null ) {
                lock = new DistributedLock(redisTemplate, id, DEFAULT_EXPIRES_MS);
                locks.put(id,lock);
            }
            return lock.acquire();
        }
        catch( Exception ex ) {
            LOGGER.warn("Exception occurred acquiring lock for id: " + id + ", allowing to be safe");
            return true;
        }
    }


    /**
     * @param id
     */

    public void release(String id) {
        try {
            DistributedLock lock = locks.get(id);
            if( lock == null ) {
                LOGGER.warn("No lock found with id: " + id);
            }
            else {
                lock.release();
            }
        }
        catch( Exception ex ) {
            LOGGER.warn("Exception occurred releasing lock for id: " + id + ", removing to be safe");
            locks.remove(id);
        }
    }

}
