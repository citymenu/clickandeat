package com.ezar.clickandeat.web.controller.helper;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component(value="requestHelper")
public class RequestHelper {
    
    @Autowired
    private OrderRepository orderRepository;
    
    /**
     * @param request
     * @return
     * @throws Exception
     */

    public Order getOrderFromSession(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(true);
        String orderid = (String)session.getAttribute("orderid");
        if( orderid == null ) {
            return null;
        }
        return orderRepository.findByOrderId(orderid);
    }

    
}
