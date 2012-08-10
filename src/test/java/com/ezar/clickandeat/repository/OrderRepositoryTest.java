package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Order;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class OrderRepositoryTest {
    
    private static final Logger LOGGER = Logger.getLogger(OrderRepositoryTest.class);

    @Autowired
    private OrderRepository repository;

    private String testOrderId = "TEST";
    
    @Before
    public void setup() throws Exception {
        removeTestOrder();
    }

    @After
    public void tearDown() throws Exception {
        removeTestOrder();
    }

    private void removeTestOrder() throws Exception {
        Order testOrder = repository.findByOrderId(testOrderId);
        if( testOrder != null ) {
            repository.delete(testOrder);
        }
    }
    
    
    @Test
    public void testSaveAndRetrieveOrder() throws Exception {
        
        Order order = new Order();
        order.setOrderId(testOrderId);
        order.setRequestedDeliveryTime(new DateTime());
        repository.save(order);
        
        Order retrievedOrder = repository.findByOrderId(testOrderId);
        Assert.assertNotNull(retrievedOrder);
    }
    
}
