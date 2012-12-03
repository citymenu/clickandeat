package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.OrderItem;
import com.ezar.clickandeat.model.OrderUpdate;
import com.ezar.clickandeat.repository.util.FilterUtils;
import com.ezar.clickandeat.util.SequenceGenerator;
import com.ezar.clickandeat.web.controller.helper.Filter;
import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.QueryUtils;
import org.springframework.util.StringUtils;

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

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    public Order create() {
        Order order = new Order();
        order.setOrderId(sequenceGenerator.getNextSequence());
        return order;
    }

    @Override
    public Order findByOrderId(String orderId) {
        long now = System.currentTimeMillis();
        Query query = query(where("orderId").is(orderId));
        Order order = operations.findOne(query,Order.class);
        if( order != null ) {
            if( order.getRestaurantId() != null ) {
                order.setRestaurant(restaurantRepository.findByRestaurantId(order.getRestaurantId()));
            }
            if( order.getVoucherId() != null ) {
                order.setVoucher(voucherRepository.findByVoucherId(order.getVoucherId()));
            }
        }
        LOGGER.debug("Retrieving order id " + orderId + " took " + ( System.currentTimeMillis() - now) + " ms");
        return order;
    }

    @Override
    public List<Order> pageByOrderId(Pageable pageable, String orderId, List<Filter> filters) {
        Query query = StringUtils.hasText(orderId)? new Query(where("orderId").regex(orderId,"i")): new Query();
        FilterUtils.applyFilters(query,filters);
        QueryUtils.applyPagination(query,pageable);
        return operations.find(query,Order.class);
    }


    @Override
    public long count(String orderId, List<Filter> filters) {
        Query query = StringUtils.hasText(orderId)? new Query(where("orderId").regex(orderId)): new Query();
        FilterUtils.applyFilters(query,filters);
        return operations.count(query,Order.class);
    }


    @Override
    public List<Order> export() {
        Query query = new Query(where("deleted").ne(true));
        query.sort().on("created", org.springframework.data.mongodb.core.query.Order.ASCENDING);
        return operations.find(query, Order.class);
    }

    
    @Override
    public Order saveOrder(Order order) {
        for( OrderItem orderItem: order.getOrderItems()) {
            if( orderItem.getOrderItemId() == null ) {
                orderItem.setOrderItemId(sequenceGenerator.getNextSequence());
            }
        }
        // Only update order costs if we have not started amending the order directly
        if( order.getOrderAmendments().size() == 0 ) {
            order.updateCosts();
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
