package com.ezar.clickandeat.scheduling;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

public class DistributedLock {

    private static final Logger LOGGER = Logger.getLogger(DistributedLock.class);
    
    private BoundValueOperations<String,String> operations;
    
    private String key;

    private final int expireMsecs = 60 * 1000;

    private boolean locked = false;

    
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

        // Sleep for a random short period to allow one instance to win each time
        long randomWait = (long)(Math.random() * 1000d);
        LOGGER.debug("Sleeping for " + randomWait + "ms");
        Thread.sleep(randomWait);

        long expires = System.currentTimeMillis() + expireMsecs;
        String expiresStr = String.valueOf(expires);
        if (operations.setIfAbsent(expiresStr)) {
            LOGGER.debug("Lock aquired");
            locked = true;
            return true;
        }

        String currentValueStr = operations.get();
        long currentTimeMillis = System.currentTimeMillis();
        LOGGER.debug("Current lock expiry: " + currentValueStr + " / Current time: " + currentTimeMillis);
        if (currentValueStr != null && Long.parseLong(currentValueStr) < currentTimeMillis) {

            // lock is expired
            String oldValueStr = operations.getAndSet(expiresStr);
            if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                LOGGER.debug("Lock aquired");
                locked = true;
                return true;
            }
            else {
                LOGGER.debug("Lock not acquired, old expiry: " + oldValueStr);
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
            LOGGER.info("Released lock");
        }
    }


}
