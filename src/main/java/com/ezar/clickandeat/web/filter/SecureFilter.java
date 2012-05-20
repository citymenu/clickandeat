package com.ezar.clickandeat.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecureFilter implements Filter {

    private boolean enabled = false;

    public void init(FilterConfig config) throws ServletException {
        String environment = System.getenv("ENVIRONMENT");
        enabled = "production".equals(environment);
    }

    public void destroy() {
    }

    /**
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if( !enabled ) {
            chain.doFilter(req,res);
            return;
        }

        String protocolHeader = req.getHeader("X-Forwarded-Proto");
        
        if(req.isSecure() || "https".equals(protocolHeader)) {
            chain.doFilter(req,res);
        }
        else {
            res.sendRedirect("https://" + req.getHeader("Host") + req.getRequestURI() + (req.getQueryString()!=null? "?" + req.getQueryString(): ""));
        }
    }
}
