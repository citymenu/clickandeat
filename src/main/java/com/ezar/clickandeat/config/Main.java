package com.ezar.clickandeat.config;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jetty.nosql.mongodb.MongoSessionIdManager;
import org.eclipse.jetty.nosql.mongodb.MongoSessionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

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
		
		// Configure mongo session manager
		MongoTemplate mongoTemplate = getMongoTemplate(props);
		MongoSessionIdManager mongoSessionIdManager = new MongoSessionIdManager(server,mongoTemplate.getCollection("sessions"));
		mongoSessionIdManager.setWorkerName("sessionManager");
		MongoSessionManager mongoSessionManager = new MongoSessionManager();
		mongoSessionManager.setSessionIdManager(mongoSessionIdManager);
		SessionHandler sessionHandler = new SessionHandler();
		sessionHandler.setSessionManager(mongoSessionManager);
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
        return mongoTemplate;
    }
    

}
