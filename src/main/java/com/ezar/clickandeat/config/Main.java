package com.ezar.clickandeat.config;

import com.ovea.jetty.session.Serializer;
import com.ovea.jetty.session.redis.RedisSessionIdManager;
import com.ovea.jetty.session.redis.RedisSessionManager;
import com.ovea.jetty.session.serializer.JdkSerializer;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.webapp.WebAppContext;
import org.joda.time.DateTimeZone;
import redis.clients.jedis.JedisPool;

import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);
	
    /**
     * @param args
     */
	
    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.load(Main.class.getResourceAsStream("/clickandeat.properties"));

        // Set the default time zone for the whole system
        String timezone = System.getenv("timezone");
        if( timezone == null ) {
            timezone = props.getProperty("timezone");
        }
        DateTimeZone.setDefault(DateTimeZone.forID(timezone));
        LOGGER.info("Set default time zone for application to: " + DateTimeZone.getDefault().getID());

        // Set default locale for the whole system
        String locale = System.getenv("locale");
        if( locale == null ) {
            locale = props.getProperty("locale");
        }
        String[] localeArray = locale.split("_");
        Locale systemLocale = new Locale(localeArray[0],localeArray[1]);
        Locale.setDefault(systemLocale);
        LOGGER.info("Set default locale for application to: " + Locale.getDefault());

        // Configure server
    	String port = (String)(System.getenv("PORT") == null? props.get("PORT"): System.getenv("PORT"));
		Server server = new Server(Integer.valueOf(port));
        server.setStopAtShutdown(true);
        server.setGracefulShutdown(5000);

        // Build security constraint
        final String realmUrlString = "src/main/webapp/WEB-INF/realm.properties";
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"admin"});
        constraint.setAuthenticate(true);

        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/admin/*");

        ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
        sh.setLoginService(new HashLoginService("llamarycomer", realmUrlString));
        sh.setConstraintMappings(new ConstraintMapping[]{cm});

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
        context.setSecurityHandler(getAuthHandler());
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


    private static SecurityHandler getAuthHandler() {

        HashLoginService l = new HashLoginService();
        l.putUser("admin", Credential.getCredential("menucha0s"), new String[] {"admin"});
        l.setName("clickandeat");

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"admin"});
        constraint.setAuthenticate(true);

        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/admin/*");

        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName("realm");
        csh.addConstraintMapping(cm);
        csh.setLoginService(l);

        return csh;

    }

}
