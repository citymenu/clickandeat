package com.ezar.clickandeat.payment;

import com.ezar.clickandeat.model.Order;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class PaymentServiceTest {
    
    private static final Logger LOGGER = Logger.getLogger(PaymentServiceTest.class);
    
    @Autowired
    private PaymentService paymentService;
    
    private Order order;
    
    @Before
    public void setup() {
        order = new Order();
        order.setTransactionId("000001");
        order.setTotalCost(18.5d);
    }

    
    @Test
    @Ignore
    public void testCancelTransaction() throws Exception {
        paymentService.processTransactionRequest(order, "3");
    }
    
    
    
}
