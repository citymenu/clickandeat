package com.ezar.clickandeat.payment;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Person;
import com.ezar.clickandeat.repository.OrderRepository;
import net.authorize.data.Customer;
import net.authorize.data.creditcard.CreditCard;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class CardPaymentServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CardPaymentServiceTest.class);

    @Autowired
    private CardPaymentService cardPaymentService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    private Order order;
    
    @Before
    public void setup() throws Exception {
        order = orderRepository.create();
        order.setTotalCost(30.5d);
        Person customer = new Person();
        customer.setFirstName("Joe");
        customer.setLastName("Pugh");
        order.setCustomer(customer);
    }

    
    @Test
    public void testBuildCardPaymentForm() throws Exception {
        cardPaymentService.submitTransactionRequest(order);
        cardPaymentService.submitTransactionRequest(order);
        LOGGER.info("DONE");
    }



    
}
