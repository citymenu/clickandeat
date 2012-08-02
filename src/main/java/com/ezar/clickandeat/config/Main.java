package com.ezar.clickandeat.config;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

import com.mongodb.WriteConcern;
import org.apache.log4j.Logger;
import org.eclipse.jetty.nosql.mongodb.MongoSessionIdManager;
import org.eclipse.jetty.nosql.mongodb.MongoSessionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;
import org.springframework.data.mongodb.core.WriteResultChecking;

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

		// Build web app context
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setDefaultsDescriptor("src/main/webapp/WEB-INF/webdefault.xml");
		context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
		context.setResourceBase("src/main/webapp");
		context.setParentLoaderPriority(true);
        context.setDistributable(true);

		// Configure mongo session id manager
		MongoTemplate mongoTemplate = getMongoTemplate(props);
		MongoSessionIdManager mongoSessionIdManager = new MongoSessionIdManager(server,mongoTemplate.getCollection("sessions"));
        mongoSessionIdManager.setScavengeDelay(0l);
        mongoSessionIdManager.setPurge(false);

        // Configure mongo session manager
		MongoSessionManager mongoSessionManager = new MongoSessionManager();
		mongoSessionManager.setSessionIdManager(mongoSessionIdManager);
        mongoSessionManager.setSaveAllAttributes(true);
        mongoSessionManager.setSavePeriod(-2); // Store attributes on login

        // Configure session handler
		SessionHandler sessionHandler = new SessionHandler();
		sessionHandler.setSessionManager(mongoSessionManager);
		context.setSessionHandler(sessionHandler);
		server.setHandler(context);
        server.setStopAtShutdown(true);
        server.setGracefulShutdown(5000);

		// Start the server
		LOGGER.info("Jetty server starting");
		server.start();
		server.join();
    }
    

    /**
     * @param props
     * @return
     */
    
    private static MongoTemplate getMongoTemplate(Properties props) throws Exception {
    	String mongoUrl = System.getenv("MONGOHQ_URL") == null? props.getProperty("MONGOHQ_URL"): System.getenv("MONGOHQ_URL");
    	String hostname = mongoUrl.split("@")[1].split(":")[0];
    	int port = Integer.valueOf(mongoUrl.split("@")[1].split(":")[1].split("/")[0]);
    	String database = mongoUrl.substring(mongoUrl.lastIndexOf("/")+1);
    	String username = mongoUrl.split("//")[1].split("@")[0].split(":")[0];
    	String password = mongoUrl.split("//")[1].split("@")[0].split(":")[1];
    	Mongo mongo = new Mongo(hostname,port);
    	MongoTemplate mongoTemplate = new MongoTemplate(mongo, database);
    	mongoTemplate.getDb().authenticate(username, password.toCharArray());
    	if( mongoTemplate.getCollection("sessions") == null ) {
    		mongoTemplate.createCollection("sessions");
    	}
        mongoTemplate.setWriteConcern(WriteConcern.REPLICAS_SAFE);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        return mongoTemplate;
    }
    

}
