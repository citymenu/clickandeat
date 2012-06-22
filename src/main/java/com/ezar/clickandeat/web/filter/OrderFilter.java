package com.ezar.clickandeat.web.filter;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.JSONUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class OrderFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(OrderFilter.class);
    
    private OrderRepository orderRepository;
    
    public void init(FilterConfig config) {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        orderRepository = context.getBean(OrderRepository.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpSession session = req.getSession(false);
        if( session != null ) {
            String orderId = (String)session.getAttribute("orderid");
            if( orderId != null ) {
                if( LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Loading order id " + orderId);
                }
                Order order = orderRepository.findByOrderId(orderId);
                if( order == null ) {
                    LOGGER.warn("Could not find order with id " + orderId);
                }
                else {
                    req.setAttribute("order",order);
                    req.setAttribute("orderjson", JSONUtils.serialize(order));
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
