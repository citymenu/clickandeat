package com.ezar.clickandeat.web.interceptor;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component(value = "orderLocaleInterceptor")
public class OrderLocaleInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = Logger.getLogger(OrderLocaleInterceptor.class);

    private static final String PARAM_NAME = "locale";

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException {

        String newLocale = request.getParameter(PARAM_NAME);
        if (newLocale != null) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            localeResolver.setLocale(request, response, StringUtils.parseLocaleString(newLocale));

            // Update order locale if present
            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            if( orderId != null ) {
                Order order = orderRepository.findByOrderId(orderId);
                if( order != null ) {
                    LOGGER.info("Updating locale for order id: " + orderId + " to " + newLocale);
                    order.setLocale(newLocale);
                    orderRepository.saveOrder(order);
                }
            }
        }

        // Proceed in any case.
        return true;
    }
    

}
