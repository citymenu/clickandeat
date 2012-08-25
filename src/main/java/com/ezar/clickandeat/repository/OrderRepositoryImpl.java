package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.OrderItem;
import com.ezar.clickandeat.model.OrderUpdate;
import com.ezar.clickandeat.util.SequenceGenerator;
import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.ORDER_STATUS_AWAITING_RESTAURANT;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private static final Logger LOGGER = Logger.getLogger(OrderRepositoryImpl.class);

    @Autowired
    private MongoOperations operations;
    
    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private RestaurantRepository restaurantRepository;


    @Override
    public Order create() {
        Order order = new Order();
        order.setOrderId(sequenceGenerator.getNextSequence());
        return order;
    }

    @Override
    public Order findByOrderId(String orderId) {
        Query query = query(where("orderId").is(orderId));
        Order order = operations.findOne(query,Order.class);
        if( order != null && order.getRestaurantId() != null ) {
            order.setRestaurant(restaurantRepository.findByRestaurantId(order.getRestaurantId()));
        }
        return order;
    }


    @Override
    public Order saveOrder(Order order) {
        order.updateCosts();
        for( OrderItem orderItem: order.getOrderItems()) {
            if( orderItem.getOrderItemId() == null ) {
                orderItem.setOrderItemId(sequenceGenerator.getNextSequence());
            }
        }
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
    public List<Order> findByOrderStatus(String orderStatus) {
        List<Order> orders = operations.find(new Query(where("orderStatus").is(ORDER_STATUS_AWAITING_RESTAURANT)),Order.class);
        for( Order order: orders ) {
            if( order.getRestaurantId() != null ) {
                order.setRestaurant(restaurantRepository.findByRestaurantId(order.getRestaurantId()));
            }
        }
        return orders;
    }
}
