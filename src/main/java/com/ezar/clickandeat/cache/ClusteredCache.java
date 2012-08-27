package com.ezar.clickandeat.cache;

import com.ezar.clickandeat.util.JSONUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component(value="clusteredCache")
public class ClusteredCache implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(ClusteredCache.class);

    private static final String TOPIC = "cache";

    private final ConcurrentMap<Class,ConcurrentMap<String,Object>> cache = new ConcurrentHashMap<Class,ConcurrentMap<String,Object>>();

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JSONUtils jsonUtils;

    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplate.setExposeConnection(true);
        CacheUpdateSubscriber subscriber = new CacheUpdateSubscriber(redisTemplate,TOPIC);
        Thread subscriberThread = new Thread(subscriber);
        subscriberThread.setDaemon(true);
        subscriberThread.start();
    }


    /**
     * @param klass
     * @param key
     * @param <T>
     * @return
     */
    
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> klass, String key ) {
        ConcurrentMap<String,Object> cacheMap = cache.get(klass);
        if( cacheMap != null ) {
            return (T)cacheMap.get(key);
        }
        return null;
    }


    /**
     * @param klass
     * @param key
     * @param object
     * @param <T>
     */

    public <T> void store(Class<T> klass, String key, T object ) {
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Putting object with key [" + key + "] into cache [" + klass.getSimpleName() + "]");
        }
        ConcurrentMap<String,Object> cacheMap = cache.get(klass);
        if( cacheMap == null ) {
            cacheMap = new ConcurrentHashMap<String, Object>();
            cache.put(klass, cacheMap);
        }
        cacheMap.put(key,object);
    }


    /**
     * @param klass
     * @param key
     * @param <T>
     */

    public <T> void remove(Class<T> klass, String key ) {
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing object with key [" + key + "] from cache [" + klass.getSimpleName() + "]");
        }

        // Wipe from this map
        removeInternal(klass,key);

        // Publish update to remove from other caches
        Map<String,String> map = new HashMap<String, String>();
        map.put("className",klass.getName());
        map.put("key",key);
        try {
            redisTemplate.getConnectionFactory().getConnection().publish(TOPIC.getBytes(), jsonUtils.serialize(map).getBytes());
        }
        catch(Exception ex) {
            LOGGER.error("Error publishing update to redis: " + ex.getMessage(),ex);
        }
    }


    /**
     * @param klass
     * @param key
     * @param <T>
     */

    public <T> void removeInternal(Class<T> klass, String key ) {
        ConcurrentMap<String,Object> cacheMap = cache.get(klass);
        if( cacheMap != null ) {
            cacheMap.remove(key);
            cache.put(klass, cacheMap);
        }
    }


    /**
     * Listens to cache updates via Redis
     */

    private final class CacheUpdateSubscriber implements Runnable, MessageListener {

        private final RedisConnection connection;

        private final byte[] channel;

        /**
         * @param template
         * @param channel
         */

        private CacheUpdateSubscriber(RedisTemplate template, String channel) {
            this.connection = template.getConnectionFactory().getConnection();
            this.channel = channel.getBytes();
        }

        @Override
        public void run() {
            connection.subscribe(this,channel);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onMessage(Message message, byte[] pattern) {
            try {
                String content = new String(message.getBody(),"utf-8");
                LOGGER.info("Received message: " + content);
                Map map = (Map) jsonUtils.deserialize(content);
                String className = (String)map.get("className");
                String key = (String)map.get("key");
                Class klass = Class.forName(className);
                removeInternal(klass,key);
            }
            catch( Exception ex ) {
                LOGGER.error("Exception in onMessage: " + ex.getMessage(),ex);
            }
        }
    }

}
