package com.ezar.clickandeat.web.security;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SecurityLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger LOGGER = Logger.getLogger(SecurityLogoutSuccessHandler.class);
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing logout success, clearing authentication");
        }

        HttpSession session = request.getSession(true);
        session.removeAttribute(SimpleSecurityContextRepository.SECURITY_CONTEXT_KEY);

        response.sendRedirect("/home.html");
    }

}
