package com.ezar.clickandeat.util;

import org.apache.log4j.Logger;
import org.junit.Test;

public class StringEscapeTest {
    
    private static final Logger LOGGER = Logger.getLogger(StringEscapeTest.class);

    @Test
    public void testEscapeSingleQuote() throws Exception {
        String name = "Papa John's";
        LOGGER.info(name.replace("'","\'"));
        LOGGER.info(name.replace("'","\\'"));
        LOGGER.info(name.replace("'","\\\'"));
        LOGGER.info(name.replace("'","\\\\'"));
    }
}
