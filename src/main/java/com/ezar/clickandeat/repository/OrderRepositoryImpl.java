package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.OrderUpdate;
import com.ezar.clickandeat.model.Person;
import com.ezar.clickandeat.util.SequenceGenerator;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.UUID;

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
        order.setOrderStatus(OrderWorkflowEngine.ORDER_STATUS_BASKET);
        order.setOrderNotificationStatus(OrderWorkflowEngine.NOTIFICATION_CALL_STATUS_NO_CALL_MADE);
        order.setUuid(UUID.randomUUID().toString());
        order.setCustomer(new Person());
        order.setDeliveryType(Order.DELIVERY);
        order.setDeliveryAddress(new Address());
        order.setBillingAddress(new Address());
        return order;
    }

    @Override
    public Order findByOrderId(String orderId) {
        Query query = query(where("orderId").is(orderId));
        query.fields().exclude("order.restaurant.menu");
        return operations.findOne(query,Order.class);
    }

    @Override
    public Order saveOrder(Order order) {
        order.updateCosts();
        operations.save(order);
        return order;
    }


    @Override
    public void addOrderUpdate(String orderId, String text) {
        OrderUpdate orderUpdate = new OrderUpdate();
        orderUpdate.setText(text);
        orderUpdate.setUpdateTime(new DateTime());
        Update update = new BasicUpdate(new BasicDBObject()).push("orderUpdates",orderUpdate);
        operations.updateFirst(query(where("orderId").is(orderId)),update,Order.class);
    }


    @Override
    public void updateOrderStatus(String orderId, String status) {
        operations.updateFirst(query(where("orderId").is(orderId)),update("orderStatus",status),Order.class);
    }



}
