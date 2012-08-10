package com.ezar.clickandeat.config;

import com.ovea.jetty.session.Serializer;
import com.ovea.jetty.session.redis.RedisSessionIdManager;
import com.ovea.jetty.session.redis.RedisSessionManager;
import com.ovea.jetty.session.serializer.JdkSerializer;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.joda.time.DateTimeZone;
import redis.clients.jedis.JedisPool;

import java.util.Properties;
import java.util.UUID;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);
	
    /**
     * @param args
     */
	
    public static void main(String[] args) throws Exception {

    	// Configure server
    	Properties props = new Properties();
    	props.load(Main.class.getResourceAsStream("/clickandeat.properties"));
    	String port = (String)(System.getenv("PORT") == null? props.get("PORT"): System.getenv("PORT"));
		Server server = new Server(Integer.valueOf(port));
        server.setStopAtShutdown(true);
        server.setGracefulShutdown(5000);

        // Set the default time zone for the whole system
        String timezone = props.getProperty("timezone");
        DateTimeZone.setDefault(DateTimeZone.forID(timezone));
        LOGGER.info("Set default time zone for application to: " + DateTimeZone.getDefault().getID());
        
		// Configure redis session id manager
        JedisPool jedisPool = getJedisPool(props);
        RedisSessionIdManager redisSessionIdManager = new RedisSessionIdManager(server,jedisPool);
        redisSessionIdManager.setWorkerName(UUID.randomUUID().toString());
        server.setSessionIdManager(redisSessionIdManager);

        // Configure redis session manager
        SessionHandler sessionHandler = new SessionHandler();
        Serializer serializer = new JdkSerializer();
        RedisSessionManager redisSessionManager = new RedisSessionManager(jedisPool,serializer);
        redisSessionManager.setSessionIdManager(redisSessionIdManager);
        sessionHandler.setSessionManager(redisSessionManager);

        // Build web app context
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDefaultsDescriptor("src/main/webapp/WEB-INF/webdefault.xml");
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("src/main/webapp");
        context.setParentLoaderPriority(true);
        context.setDistributable(true);
        context.setSessionHandler(sessionHandler);
        server.setHandler(context);

		// Start the server
		LOGGER.info("Jetty server starting");
		server.start();
		server.join();
    }
    

    /**
     * @param props
     * @return
     * @throws Exception
     */
    
    private static JedisPool getJedisPool(Properties props) throws Exception {
        String redisUrl = System.getenv("REDISTOGO_URL") == null? props.getProperty("REDISTOGO_URL"): System.getenv("REDISTOGO_URL");
        String hostname = redisUrl.split("@")[1].split(":")[0];
        int port = Integer.valueOf(redisUrl.split("@")[1].split(":")[1].split("/")[0]);
        String password = redisUrl.split("//")[1].split("@")[0].split(":")[1];
        int timeout = Integer.valueOf(props.getProperty("redis.pool.timeout"));

        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = Integer.valueOf(props.getProperty("redis.pool.maxActive"));
        config.testOnBorrow = Boolean.valueOf(props.getProperty("redis.pool.testOnBorrow"));

        return new JedisPool(config,hostname,port, timeout, password);
    }

}
