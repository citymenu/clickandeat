package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.web.controller.helper.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {

    Order create();

    Order findByOrderId(String orderId);

    Order saveOrder(Order order);

    void addOrderUpdate(String orderId, String orderUpdate);
    
    List<Order> findByOrderStatus(String orderStatus);

    List<Order> export();
    
    List<Order> pageByOrderId(Pageable pageable, String orderId, List<Filter> filters);
    
    long count(String orderId, List<Filter> filters);
}
