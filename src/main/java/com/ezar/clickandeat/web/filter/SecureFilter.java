package com.ezar.clickandeat.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecureFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
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

        String protocolHeader = req.getHeader("X-Forwarded-Proto");
        
        if(req.isSecure() || protocolHeader == null || "https".equals(protocolHeader)) {
            chain.doFilter(req,res);
        }
        else {
            res.sendRedirect("https://" + req.getHeader("Host") + req.getRequestURI() + (req.getQueryString()!=null? "?" + req.getQueryString(): ""));
        }
    }
}
