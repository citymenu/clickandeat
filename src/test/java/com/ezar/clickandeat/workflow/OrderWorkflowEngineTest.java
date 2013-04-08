package com.ezar.clickandeat.workflow;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.OrderRepository;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.ezar.clickandeat.workflow.OrderWorkflowEngine.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})

public class OrderWorkflowEngineTest {

    private static final Logger LOGGER = Logger.getLogger(OrderWorkflowEngineTest.class);

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;

    @Autowired
    private WorkflowStatusExceptionMessageResolver resolver;
    
    @Autowired
    private OrderRepository orderRepository;
                  
    private Order order;
    
    private String orderId = "testorder";
    
    @Before
    public void setup() throws Exception {
        order = orderRepository.findByOrderId(orderId);
        if( order != null ) {
            orderRepository.delete(order);
        }
        order = new Order();
        order.setOrderId(orderId);
        Restaurant restauarant = new Restaurant();
        restauarant.setName("Pizza Express");
        restauarant.setRestaurantId("00000002");
        order.setRestaurantId(restauarant.getRestaurantId());
        order.setRestaurant(restauarant);
    }

    
    
    
    /**
     * @throws Exception
     */
    @Test
    public void testCustomerAttemptsToCancelAcceptedOrder() throws Exception {

        order.setOrderStatus(ORDER_STATUS_RESTAURANT_ACCEPTED);
        
        try {
            orderWorkflowEngine.processAction(order,ACTION_CUSTOMER_CANCELS);
            fail("WorkflowStatusException should be thrown");
        }
        catch( Exception ex ) {
            assertTrue("Exception should be WorkflowStatusException", ex instanceof WorkflowStatusException);
            WorkflowStatusException wex = (WorkflowStatusException)ex;
            String error = resolver.getWorkflowStatusExceptionMessage(wex);
            LOGGER.info("Resolved exception message as [" + error + "]");
        }
    }


    /**
     * @throws Exception
     */
    @Test
    public void testSystemAttemptsToCancelCancelledOrder() throws Exception {

        order.setOrderStatus(ORDER_STATUS_AUTO_CANCELLED);

        try {
            orderWorkflowEngine.processAction(order,ACTION_SYSTEM_CANCELS);
            fail("WorkflowStatusException should be thrown");
        }
        catch( Exception ex ) {
            assertTrue("Exception should be WorkflowStatusException", ex instanceof WorkflowStatusException);
            WorkflowStatusException wex = (WorkflowStatusException)ex;
            String error = resolver.getWorkflowStatusExceptionMessage(wex);
            LOGGER.info("Resolved exception message as [" + error + "]");
        }

    }

    
    
}


