package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Person;
import com.ezar.clickandeat.util.SequenceGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(OrderRepositoryImpl.class);
    
    @Autowired
    private MongoOperations operations;
    
    @Autowired
    private SequenceGenerator sequenceGenerator;
    
    @Override
    public Order create() {
        Order order = new Order();
        order.setOrderId(sequenceGenerator.getNextSequence());
        order.setCustomer(new Person());
        order.setDeliveryAddress(new Address());
        order.setBillingAddress(new Address());
        return order;
    }

    @Override
    public Order findByOrderId(String orderId) {
        return operations.findOne(query(where("orderId").is(orderId)),Order.class);
    }

    @Override
    public Order saveOrder(Order order) {
        order.updateCosts();
        operations.save(order);
        return order;
    }
}
