package com.ezar.clickandeat.templating;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Map;

@Component(value="velocityTemplatingService")
public class VelocityTemplatingService implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(VelocityTemplatingService.class);

    private static final String VELOCITY_LOGGER_NAME = "VelocityLogger";

    private VelocityEngine engine;


    @Override
    public void afterPropertiesSet() throws Exception {
        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        engine.setProperty("runtime.log.logsystem.log4j.logger",VELOCITY_LOGGER_NAME);
        engine.setProperty("resource.loader","class");
        engine.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        engine.init();
    }


    /**
     * @param model
     * @param templateLocation
     * @return
     * @throws Exception
     */
    
    public String mergeContentIntoTemplate(Map<String,Object> model, String templateLocation) throws Exception {
    
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Merging content into template location: " + templateLocation);
        }

        VelocityContext context = new VelocityContext();
        if( model != null ) {
            for(Map.Entry<String,Object> entry: model.entrySet()) {
                context.put(entry.getKey(),entry.getValue());
            }
        }

        StringWriter sw = new StringWriter();
        engine.mergeTemplate(templateLocation,"utf-8",context,sw);
        return sw.toString();
    }
    
    
}
