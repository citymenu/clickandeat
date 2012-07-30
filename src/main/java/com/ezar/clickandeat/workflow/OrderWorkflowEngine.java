package com.ezar.clickandeat.workflow;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.workflow.handler.IWorkflowHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Component(value = "orderWorkflowEngine")
public class OrderWorkflowEngine implements ApplicationContextAware, InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(OrderWorkflowEngine.class);

    /**
     * Order status values
     */
    
    public static final String ORDER_STATUS_BASKET = "BASKET";
    public static final String ORDER_STATUS_AWAITING_RESTAURANT = "AWAITING_RESTAURANT";
    public static final String ORDER_STATUS_RESTAURANT_ACCEPTED = "RESTAURANT_ACCEPTED";
    public static final String ORDER_STATUS_RESTAURANT_DECLINED = "RESTAURANT_DECLINED";
    public static final String ORDER_STATUS_RESTAURANT_CANCELLED = "RESTAURANT_CANCELLED";
    public static final String ORDER_STATUS_CUSTOMER_CANCELLED = "CUSTOMER_CANCELLED";
    public static final String ORDER_STATUS_AUTO_CANCELLED = "AUTO_CANCELLED";


    /**
     * Action values relating to order status
     */
    
    public static final String ACTION_ORDER_PLACED = "ORDER_PLACED";
    public static final String ACTION_CALL_RESTAURANT = "CALL_RESTAURANT";
    public static final String ACTION_RESTAURANT_ACCEPTED = "RESTAURANT_ACCEPTED";
    public static final String ACTION_RESTAURANT_ACCEPTED_WITH_DELIVERY_DETAIL = "RESTAURANT_ACCEPTED_WITH_DELIVERY_DETAIL";
    public static final String ACTION_RESTAURANT_DECLINED = "RESTAURANT_DECLINED";
    public static final String ACTION_RESTAURANT_CANCELLED = "RESTAURANT_CANCELLED";
    public static final String ACTION_CUSTOMER_CANCELLED = "CUSTOMER_CANCELLED";
    public static final String ACTION_AUTO_CANCELLED = "AUTO_CANCELLED";


    /**
     * Order notification call status values
     */

    public static final String NOTIFICATION_CALL_STATUS_NO_CALL_MADE = "NO_CALL_MADE";
    public static final String NOTIFICATION_CALL_STATUS_CALL_IN_PROGRESS = "NOTIFICATION_CALL_STATUS_CALL_IN_PROGRESS";
    public static final String NOTIFICATION_CALL_STATUS_RESTAURANT_ANSWERED = "ANSWERED";
    public static final String NOTIFICATION_CALL_STATUS_RESTAURANT_NO_ANSWER = "NO_ANSWER";
    public static final String NOTIFICATION_CALL_STATUS_RESTAURANT_FAILED_TO_RESPOND = "FAILED_TO_RESPOND";
    public static final String NOTIFICATION_CALL_STATUS_ERROR = "ERROR";


    /**
     * Action values relating to order notification status
     */

    public static final String ACTION_NOTIFICATION_SMS_SENT = "NOTIFICATION_SMS_SENT";
    public static final String ACTION_NOTIFICATION_CALL_ANSWERED = "NOTIFICATION_CALL_PLACED";
    public static final String ACTION_NOTIFICATION_CALL_NO_ANSWER = "NOTIFICATION_CALL_NO_ANSWER";
    public static final String ACTION_NOTIFICATION_CALL_ERROR = "NOTIFICATION_CALL_ERROR";
        

    @Autowired
    private OrderRepository orderRepository;

    private Map<String,IWorkflowHandler> workflowHandlerMap = new HashMap<String, IWorkflowHandler>();

    private ApplicationContext applicationContext;


    @Override
    public void afterPropertiesSet() throws Exception {
        for(IWorkflowHandler handler: applicationContext.getBeansOfType(IWorkflowHandler.class).values()) {
            workflowHandlerMap.put(handler.getWorkflowAction(),handler);
        }
    }


    /**
     * @param order
     * @param action
     * @return
     * @throws WorkflowException
     */

    public Order processAction(Order order, String action) throws WorkflowException {
        return processAction(order,action,null);
    }
    
    
    /**
     * @param order
     * @param action
     * @param context
     * @return
     * @throws WorkflowException
     */
    
    public Order processAction(Order order, String action, Map<String,Object> context ) throws WorkflowException {
        
        if( order == null ) {
            throw new IllegalArgumentException("Order must not be null");    
        }
        
        if( !StringUtils.hasText(action)) {
            throw new IllegalArgumentException("action must not be null");
        }
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing action [" + action + "] for order id [" + order.getOrderId() + "]");
        }
        
        IWorkflowHandler handler = workflowHandlerMap.get(action);
        if( handler == null ) {
            throw new WorkflowException("errors.workflow.no-handler-mapped");
        }

        try {
            order = handler.handle(order,context);
            order = orderRepository.saveOrder(order);
        }
        catch( Exception ex ) {
            order.addOrderUpdate("Exception processing workflow update: " + ex.getMessage());
        }
        
        order = orderRepository.saveOrder(order);
        return order;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
