package com.ezar.clickandeat.payment;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.util.NumberUtil;
import com.ezar.clickandeat.util.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

@Component(value = "cardPaymentService")
public class CardPaymentService {

    private static final Logger LOGGER = Logger.getLogger(CardPaymentService.class);

    private String virtualPosRequestUrl;
    
    private String virtualPosUrl;

    private String responseUrl;
    
    private String currencyCode;

    private String merchantCode;

    private String terminalNumber;
    
    private String customerLanguage;

    private String secretCode;

    /**
     * @param order
     * @throws Exception
     */

    public void buildCardPaymentForm(Order order) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = buildRequest(order);
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            LOGGER.info(line);
        }
    }


    /**
     * @param order
     * @return
     */
    
    private HttpPost buildRequest( Order order ) throws Exception {
        HttpPost request = new HttpPost(virtualPosRequestUrl);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_Amount", NumberUtil.formatForCardPayment(order.getTotalCost())));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_Currency", currencyCode));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_Order", order.getOrderId()));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_ProductDescription","Online food order"));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_CardHolder",order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_MerchantCode",merchantCode));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_ConsumerLanguage",customerLanguage));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_Terminal",terminalNumber));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_TransactionType","0"));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_MerchantSignature",buildSignature(order)));
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return request;
    }


    /**
     * @param order
     * @return
     */

    private String buildSignature(Order order) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(NumberUtil.formatForCardPayment(order.getTotalCost()));
        sb.append(order.getOrderId());
        sb.append(merchantCode);
        sb.append(currencyCode);
        sb.append("0");
        sb.append(secretCode);

        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] hash = sha1.digest(sb.toString().getBytes("utf-8"));
        return byteArray2Hex(hash);
    }


    /**
     * @param hash
     * @return
     */

    private String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }





    @Required
    @Value(value="${payment.virtualPosRequestUrl}")
    public void setVirtualPosRequestUrl(String virtualPosRequestUrl) {
        this.virtualPosRequestUrl = virtualPosRequestUrl;
    }

    @Required
    @Value(value="${payment.virtualPosUrl}")
    public void setVirtualPosUrl(String virtualPosUrl) {
        this.virtualPosUrl = virtualPosUrl;
    }

    @Required
    @Value(value="${payment.responseUrl}")
    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    @Required
    @Value(value="${payment.currencyCode}")
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Required
    @Value(value="${payment.merchantCode}")
    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    @Required
    @Value(value="${payment.terminalNumber}")
    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    @Required
    @Value(value="${payment.customerLanguage}")
    public void setCustomerLanguage(String customerLanguage) {
        this.customerLanguage = customerLanguage;
    }

    @Required
    @Value(value="${payment.secretCode}")
    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
