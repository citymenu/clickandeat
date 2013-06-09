package com.ezar.clickandeat.util;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

public class DistributedLock {

    private static final Logger LOGGER = Logger.getLogger(DistributedLock.class);
    
    private static final int DEFAULT_EXPIRE_MS = 1000 * 50;
    
    private BoundValueOperations<String,String> operations;
    
    private String key;

    private int expireMsecs = DEFAULT_EXPIRE_MS;

    private boolean locked = false;


    /**
     * @param template
     * @param key
     */

    public DistributedLock( StringRedisTemplate template, String key, int expireMsecs ) {
        this.operations = template.boundValueOps(key);
        this.key = key;
        this.expireMsecs = expireMsecs;
    }

    
    /**
     * @param template
     * @param key
     */

    public DistributedLock( StringRedisTemplate template, String key ) {
        this.operations = template.boundValueOps(key);
        this.key = key;
    }


    /**
     * Gets the lock
     * @return
     * @throws InterruptedException
     */
    
    public synchronized boolean acquire() throws InterruptedException {

        LOGGER.debug("Attempting to acquire lock");

        long expires = System.currentTimeMillis() + expireMsecs;
        String expiresStr = String.valueOf(expires);
        if (operations.setIfAbsent(expiresStr)) {
            LOGGER.debug("Lock aquired");
            locked = true;
            return true;
        }

        String currentValueStr = operations.get();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentValueStr != null && Long.parseLong(currentValueStr) < currentTimeMillis) {

            // lock is expired
            String oldValueStr = operations.getAndSet(expiresStr);
            if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                LOGGER.debug("Lock aquired");
                locked = true;
                return true;
            }
        }

        LOGGER.debug("Lock not aquired");
        return false;
    }


    /**
     * Acqurired lock release.
     */

    public synchronized void release() {
        if (locked) {
            operations.getOperations().delete(key);
            locked = false;
            LOGGER.debug("Released lock");
        }
    }


}
