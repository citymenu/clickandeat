package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Person;
import com.ezar.clickandeat.util.SequenceGenerator;
import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;


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

    @Override
    public void addOrderUpdate(String orderId, String orderUpdate) {
        Update update = new BasicUpdate(new BasicDBObject()).push("orderUpdates",orderUpdate);
        operations.updateFirst(query(where("orderId").is(orderId)),update,Order.class);
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        operations.updateFirst(query(where("orderId").is(orderId)),update("status",status),Order.class);
    }
}
