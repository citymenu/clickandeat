package com.ezar.clickandeat.config;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.*;

public class MessageFactory {
    
    private static final Map<String,String> messages;

    private static final String systemLocale;
    
    private static final Locale applicationLocale;
    
    static {
        try {
            String locale = System.getenv("locale");
            if( locale == null ) {
                Resource resource = new ClassPathResource("/clickandeat.properties");
                Properties props = new Properties();
                props.load(resource.getInputStream());
                locale = props.getProperty("locale");
            }
            Resource resource = new ClassPathResource("/messages_" + locale.split("_")[0] + ".properties");
            Properties props = new Properties();
            props.load(resource.getInputStream());
            systemLocale = locale;
            applicationLocale = new Locale(locale.split("_")[0],locale.split("_")[1]);
            messages = new HashMap<String, String>();
            for( Object key: props.keySet()) {
                messages.put((String)key, (String)props.get(key));
            }
        }
        catch( Exception ex ) {
            throw new RuntimeException(ex);
        }
    }

    public static Locale getLocale() {
        return applicationLocale;
    }
    
    public static String getLocaleString() {
        return systemLocale; 
    }

    /**
     * @param key
     * @return
     */

    public static String getMessage(String key, boolean escapeHtml) {
        if( !messages.containsKey(key)) {
            return "missing:" + key;
        }
        String value = messages.get(key);
        return escapeHtml? StringEscapeUtils.escapeHtml(value): value;
    }


    /**
     * @param key
     * @param escapeHtml
     * @param args
     * @return
     */
    
    public static String formatMessage(String key, boolean escapeHtml, Object... args ) {
        if( !messages.containsKey(key)) {
            return key;
        }
        String value = MessageFormat.format(messages.get(key),args);
        return escapeHtml? StringEscapeUtils.escapeHtml(value): value;
    }
    
    
    /**
     * @return
     */

    public static Set<Map.Entry<String,String>> getMessages() {
        return messages.entrySet();
    }
    
    
}
