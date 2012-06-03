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

@Component(value="simpleSecurityContextRepository")
public class SimpleSecurityContextRepository implements SecurityContextRepository {

    private static final Logger LOGGER = Logger.getLogger(SimpleSecurityContextRepository.class);
    
    public static final String SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    
    
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {

        HttpSession session = requestResponseHolder.getRequest().getSession(false);
        SecurityContext securityContext = null;
        
        if( session == null ) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("HttpSession is null, not returning new empty security context");
            }
            return SecurityContextHolder.createEmptyContext();
        }

        securityContext = (SecurityContext)session.getAttribute(SECURITY_CONTEXT_KEY);
        if( securityContext == null ) {
            if( LOGGER.isDebugEnabled()) {
                LOGGER.debug("No security context found for session attribute " + SECURITY_CONTEXT_KEY + " returning new empty context");
            }
            return SecurityContextHolder.createEmptyContext();
        }

        return securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Saving security context into HttpSession");
        }
        request.getSession(true).setAttribute(SECURITY_CONTEXT_KEY,context);
    }

    
    @Override
    public boolean containsContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SECURITY_CONTEXT_KEY) != null;
    }

}
