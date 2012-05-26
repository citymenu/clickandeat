package com.ezar.clickandeat.payment;

import net.authorize.Environment;
import net.authorize.Merchant;
import net.authorize.TransactionType;
import net.authorize.aim.Result;
import net.authorize.aim.Transaction;
import net.authorize.data.Customer;
import net.authorize.data.creditcard.CreditCard;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component(value = "paymentService")
public class PaymentService implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(PaymentService.class);

    private String apiLoginId;
    
    private String transactionKey;
    
    private String currencyCode;

    private Merchant merchant;

    @Override
    public void afterPropertiesSet() throws Exception {
        merchant = Merchant.createMerchant(Environment.SANDBOX, apiLoginId, transactionKey);
    }


    /**
     * @param creditCard
     * @param amount
     * @return
     */
    public PaymentResult authorizePayment(CreditCard creditCard, Double amount) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Authorizing payment " + amount + " on card [" + creditCard + "]");
        }

        // Create transaction
        Transaction transaction = merchant.createAIMTransaction(TransactionType.AUTH_ONLY, new BigDecimal(amount));
        transaction.setCreditCard(creditCard);

        // Get processing result
        Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);

        // Convert to payment result
        return buildPaymentResult(result);
    }


    /**
     * @param creditCard
     * @param amount
     * @return

     */
    public PaymentResult authorizeAndCapturePayment(CreditCard creditCard, Double amount ) {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Authorizing and capturing payment " + amount + " on card [" + creditCard + "]");
        }

        // Create transaction
        Transaction transaction = merchant.createAIMTransaction(TransactionType.AUTH_CAPTURE, new BigDecimal(amount));
        transaction.setCreditCard(creditCard);

        // Get processing result
        Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);
        
        // Convert to payment result
        return buildPaymentResult(result);
    }


    /**
     * @param transactionId
     * @param amount
     * @return
     */

    public PaymentResult captureAuthorizedTransaction(String transactionId, Double amount) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Capturing authorized payment on transaction id " + transactionId);
        }

        // Create transaction
        Transaction transaction = merchant.createAIMTransaction(TransactionType.PRIOR_AUTH_CAPTURE, new BigDecimal(amount));
        transaction.setTransactionId(transactionId);

        // Get processing result
        Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);

        // Convert to payment result
        return buildPaymentResult(result);
    }


    /**
     * @param transactionId
     * @param amount
     * @return
     */

    public PaymentResult voidTransaction(String transactionId, Double amount ) {
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Voiding transaction " + transactionId);
        }

        // Create transaction
        Transaction transaction = merchant.createAIMTransaction(TransactionType.VOID, new BigDecimal(amount));
        transaction.setTransactionId(transactionId);

        // Get processing result
        Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);

        // Convert to payment result
        return buildPaymentResult(result);
    }


    /**
     * @param result
     * @return
     */
    private PaymentResult buildPaymentResult(Result<Transaction> result) {
        
        PaymentResult paymentResult = new PaymentResult();

        if( result.getTarget() != null ) {
            paymentResult.setTransactionId(result.getTarget().getTransactionId());
            paymentResult.setAuthorizationCode(result.getTarget().getAuthorizationCode());
        }

        paymentResult.setApproved(result.isApproved());
        paymentResult.setDeclined(result.isDeclined());
        paymentResult.setError(result.isError());
        paymentResult.setReview(result.isReview());
        paymentResult.setResponseText(result.getResponseText());

        if( result.getResponseCode() != null ) {
            paymentResult.setResponseCode(result.getResponseCode().getCode());
            paymentResult.setResponseDescription(result.getResponseCode().getDescription());
        }
        
        if( result.getReasonResponseCode() != null ) {
            paymentResult.setReasonResponseCode(result.getReasonResponseCode().getResponseReasonCode());
            paymentResult.setReasonResponseText(result.getReasonResponseCode().getReasonText());

            if( result.getReasonResponseCode().getResponseCode() != null ) {
                paymentResult.setReasonResponseNotes(result.getReasonResponseCode().getNotes());
                paymentResult.setReasonResponseResponseCode(result.getReasonResponseCode().getResponseReasonCode());
                paymentResult.setReasonResponseResponseDescription(result.getReasonResponseCode().getReasonText());
            }
        }

        return paymentResult;
    }
    
    

    @Required
    @Value(value="${payment.loginId}")
    public void setApiLoginId(String apiLoginId) {
        this.apiLoginId = apiLoginId;
    }

    @Required
    @Value(value="${payment.transactionKey}")
    public void setTransactionKey(String transactionKey) {
        this.transactionKey = transactionKey;
    }

    @Required
    @Value(value="${payment.currencyCode}")
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

}
