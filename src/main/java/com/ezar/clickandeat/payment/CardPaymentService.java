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

@Component(value = "cardPaymentService")
public class CardPaymentService implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(CardPaymentService.class);

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

    public CardPaymentResult authorizeAndCapturePayment(Customer customer, CreditCard creditCard, Double amount ) {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Authorizing and capturing payment " + amount + " on card [" + creditCard + "]");
        }

        // Create transaction
        Transaction transaction = merchant.createAIMTransaction(TransactionType.AUTH_CAPTURE, new BigDecimal(amount));
        transaction.setCustomer(customer);
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

    public CardPaymentResult voidTransaction(String transactionId, Double amount ) {
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
    private CardPaymentResult buildPaymentResult(Result<Transaction> result) {
        
        CardPaymentResult cardPaymentResult = new CardPaymentResult();

        if( result.getTarget() != null ) {
            cardPaymentResult.setTransactionId(result.getTarget().getTransactionId());
            cardPaymentResult.setAuthorizationCode(result.getTarget().getAuthorizationCode());
        }

        cardPaymentResult.setApproved(result.isApproved());
        cardPaymentResult.setDeclined(result.isDeclined());
        cardPaymentResult.setError(result.isError());
        cardPaymentResult.setReview(result.isReview());
        cardPaymentResult.setResponseText(result.getResponseText());

        if( result.getResponseCode() != null ) {
            cardPaymentResult.setResponseCode(result.getResponseCode().getCode());
            cardPaymentResult.setResponseDescription(result.getResponseCode().getDescription());
        }
        
        if( result.getReasonResponseCode() != null ) {
            cardPaymentResult.setReasonResponseCode(result.getReasonResponseCode().getResponseReasonCode());
            cardPaymentResult.setReasonResponseText(result.getReasonResponseCode().getReasonText());

            if( result.getReasonResponseCode().getResponseCode() != null ) {
                cardPaymentResult.setReasonResponseNotes(result.getReasonResponseCode().getNotes());
                cardPaymentResult.setReasonResponseResponseCode(result.getReasonResponseCode().getResponseReasonCode());
                cardPaymentResult.setReasonResponseResponseDescription(result.getReasonResponseCode().getReasonText());
            }
        }

        return cardPaymentResult;
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
