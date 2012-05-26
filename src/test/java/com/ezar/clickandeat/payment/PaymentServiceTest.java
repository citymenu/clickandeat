package com.ezar.clickandeat.payment;

import net.authorize.data.creditcard.CreditCard;
import org.apache.log4j.Logger;
import org.junit.Assert;
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

    @Test
    public void testAuthorizeAndCapturePayment() throws Exception {

        CreditCard creditCard = buildCreditCard();
        Double amount = 100d;
        
        PaymentResult result = paymentService.authorizeAndCapturePayment(creditCard, amount);
        LOGGER.info(result);
        Assert.assertTrue("Payment should be approved", result.isApproved());

    }

    
    @Test
    public void testAuthorizeThenCapturePayment() throws Exception {

        CreditCard creditCard = buildCreditCard();
        Double amount = 75d;

        // Authorize payment
        PaymentResult result = paymentService.authorizePayment(creditCard, amount);
        LOGGER.info(result);
        Assert.assertTrue("Payment should be approved", result.isApproved());
        
        String transactionId = result.getTransactionId();
        LOGGER.info("Got transaction id: " + transactionId);
        Assert.assertTrue("Transaction id should not be null", transactionId != null );

        // Capture payment
        PaymentResult captureResult = paymentService.captureAuthorizedTransaction(transactionId,amount);
        LOGGER.info(captureResult);
        Assert.assertTrue("Payment should be approved", captureResult.isApproved());

    }


    @Test
    public void testAuthorizeThenVoidTransaction() throws Exception {

        CreditCard creditCard = buildCreditCard();
        Double amount = 5d;

        // Authorize payment
        PaymentResult result = paymentService.authorizePayment(creditCard, amount);
        LOGGER.info(result);
        Assert.assertTrue("Payment should be approved", result.isApproved());

        String transactionId = result.getTransactionId();
        LOGGER.info("Got transaction id: " + transactionId);
        Assert.assertTrue("Transaction id should not be null", transactionId != null );

        // Void payment
        PaymentResult voidResult = paymentService.voidTransaction(transactionId, amount);
        LOGGER.info(voidResult);
        Assert.assertTrue("Payment should be voided", voidResult.isApproved());

    }


    private CreditCard buildCreditCard() {
        CreditCard creditCard = CreditCard.createCreditCard();
        creditCard.setCreditCardNumber("4012888888881881");
        creditCard.setExpirationMonth("10");
        creditCard.setExpirationYear("2014");
        return creditCard;
    }

}
