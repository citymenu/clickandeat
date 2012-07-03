package com.ezar.clickandeat.web.filter;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.SearchRepository;
import com.ezar.clickandeat.util.JSONUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SearchFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(SearchFilter.class);
    
    private SearchRepository searchRepository;
    
    public void init(FilterConfig config) {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        searchRepository = context.getBean(SearchRepository.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpSession session = req.getSession(false);
        if( session != null ) {
            String searchId = (String)session.getAttribute("searchid");
            if( searchId != null ) {
                if( LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Loading search id " + searchId);
                }
                Search search = searchRepository.findBySearchId(searchId);
                if( search == null ) {
                    LOGGER.warn("Could not find search with id " + searchId);
                }
                else {
                    req.setAttribute("search",search);
                }
            }
        }
        chain.doFilter(request,response);
    }


    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
