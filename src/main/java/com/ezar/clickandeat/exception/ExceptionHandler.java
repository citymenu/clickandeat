package com.ezar.clickandeat.exception;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component(value="exceptionHandler")
public class ExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class);
    
    /**
     * @param ex
     */
    
    public void handleException(Exception ex ) {
        LOGGER.error("Handling exception",ex);
    }
    
}
