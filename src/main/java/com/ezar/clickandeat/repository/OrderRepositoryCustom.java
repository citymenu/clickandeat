package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;

public interface OrderRepositoryCustom {

    Order create();

    Order findByOrderId(String orderId);

    Order saveOrder(Order order);

    void addOrderUpdate(String orderId, String orderUpdate);
    
    void updateOrderStatus(String orderId, String status);
    
}
