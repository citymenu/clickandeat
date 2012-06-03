package com.ezar.clickandeat.web.security;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(value="simpleSecurityContextRepository")
public class SimpleSecurityContextRepository implements SecurityContextRepository {

    private static final Logger LOGGER = Logger.getLogger(SimpleSecurityContextRepository.class);
    
    public static final String SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    
    private final Map<String,Integer> securityContextHashCodeMap = new ConcurrentHashMap<String, Integer>();
    

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {

        HttpSession session = requestResponseHolder.getRequest().getSession(false);
        
        if( session == null ) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("HttpSession is null, not returning new empty security context");
            }
            return SecurityContextHolder.createEmptyContext();
        }

        String sessionId = session.getId();
        SecurityContext securityContext = (SecurityContext)session.getAttribute(SECURITY_CONTEXT_KEY);
        if( securityContext == null ) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("No security context found for session attribute " + SECURITY_CONTEXT_KEY + " returning new empty context");
            }
            securityContext = SecurityContextHolder.createEmptyContext();
        }
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Storing security context hashcode for session id [" + sessionId + "]");
        }
        securityContextHashCodeMap.put(sessionId,securityContext.hashCode());

        return securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {

        String sessionId = null;
        
        try {
            boolean saveContext = false;            
            
            HttpSession session = request.getSession(true);
            sessionId = session.getId();

            Integer contextHash = context.hashCode();
            Integer storedHash = securityContextHashCodeMap.get(sessionId);

            if( storedHash == null ) {
                if( LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No security context hash stored for session id [" + sessionId + "], will persist context");
                }
                saveContext = true;
            }
            else if( !storedHash.equals(contextHash )) {
                if( LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Context has been modified since loading for session id [" + sessionId + "], will persist context");
                }
                saveContext = true;
            }
            else {
                if( LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No changes made to security context for session id [" + sessionId + "] not persisting context");
                }
            }
            
            if( saveContext) {            
                session.setAttribute(SECURITY_CONTEXT_KEY,context);
            }
        }
        finally {
            if( sessionId != null ) {
                if( LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Clearing sessionId [" + sessionId + "] from map");
                }
                securityContextHashCodeMap.remove(sessionId);
            }
        }
    }

    
    @Override
    public boolean containsContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SECURITY_CONTEXT_KEY) != null;
    }

    
}
