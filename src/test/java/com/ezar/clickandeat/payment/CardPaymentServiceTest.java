package com.ezar.clickandeat.payment;

import net.authorize.data.Customer;
import net.authorize.data.creditcard.CreditCard;
import org.apache.log4j.Logger;
import org.junit.Assert;
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

    private Customer customer = buildCustomer();
    private CreditCard creditCard = buildCreditCard();


    @Test
    public void testAuthorizeAndCapturePayment() throws Exception {
        Double amount = 100d;
        CardPaymentResult result = cardPaymentService.authorizeAndCapturePayment(customer, creditCard, amount);
        LOGGER.info(result);
        Assert.assertTrue("Payment should be approved", result.isApproved());

    }


    @Test
    public void testAuthorizeAndCaptureThenVoidTransaction() throws Exception {

        Double amount = 5d;

        // Authorize payment
        CardPaymentResult result = cardPaymentService.authorizeAndCapturePayment(customer, creditCard, amount);
        LOGGER.info(result);
        Assert.assertTrue("Payment should be approved", result.isApproved());

        String transactionId = result.getTransactionId();
        LOGGER.info("Got transaction id: " + transactionId);
        Assert.assertTrue("Transaction id should not be null", transactionId != null );

        // Void payment
        CardPaymentResult voidResult = cardPaymentService.voidTransaction(transactionId, amount);
        LOGGER.info(voidResult);
        Assert.assertTrue("Payment should be voided", voidResult.isApproved());

    }


    private CreditCard buildCreditCard() {
        CreditCard creditCard = CreditCard.createCreditCard();
        creditCard.setCreditCardNumber("4012888888881881");
        creditCard.setExpirationMonth("10");
        creditCard.setExpirationYear("2014");
        creditCard.setCardCode("333");
        return creditCard;
    }


    private Customer buildCustomer() {
        Customer customer = Customer.createCustomer();
        customer.setFirstName("Joe");
        customer.setLastName("Pugh");
        customer.setAddress("80 Peel Road");
        customer.setCity("London");
        customer.setZipPostalCode("E18 2LG");
        return customer;
    }
    
    
}
