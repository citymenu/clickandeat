package com.ezar.clickandeat.payment;


import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.util.NumberUtil;
import com.ezar.clickandeat.util.SequenceGenerator;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.*;

@Component(value="paymentService")
public class PaymentService {

    private static final Logger LOGGER = Logger.getLogger(PaymentService.class);

    @Autowired
    private SequenceGenerator sequenceGenerator;
    
    private XMLOutputter outputter;

    private String virtualPosRequestUrl;

    private String virtualPosUrl;

    private String baseUrl;

    private String paymentBaseUrl;
    
    private String currencyCode;

    private String merchantCode;

    private String terminalNumber;

    private String customerLanguage;

    private String secretCode;


    public PaymentService() {
        Format formatter = Format.getPrettyFormat();
        formatter.setExpandEmptyElements(true);
        formatter.setOmitDeclaration(true);
        formatter.setOmitEncoding(true);
        outputter = new XMLOutputter(formatter);
    }


    /**
     * @param order
     * @return
     * @throws Exception
     */

    public String buildTransactionRequest(Order order, String transactionType) throws Exception {
        Document document = new Document(new Element("DATOSENTRADA"));
        Element root = document.getRootElement();
        List<Element> children = root.getChildren();
        children.add(createElement("DS_Version","1.0"));
        children.add(createElement("DS_MERCHANT_AMOUNT",NumberUtil.formatForCardPayment(order.getTotalCost())));
        children.add(createElement("DS_MERCHANT_CURRENCY",currencyCode));
        children.add(createElement("DS_MERCHANT_ORDER",order.getTransactionId()));
        children.add(createElement("DS_MERCHANT_MERCHANTCODE",merchantCode));
        children.add(createElement("DS_MERCHANT_MERCHANTSIGNATURE","1.0"));
        children.add(createElement("DS_MERCHANT_TERMINAL","1.0"));
        children.add(createElement("DS_MERCHANT_TRANSACTIONTYPE",transactionType));
        children.add(createElement("DS_MERCHANT_MERCHANTDATA","1.0"));
        children.add(createElement("DS_MERCHANT_PAN","1.0"));
        children.add(createElement("DS_MERCHANT_EXPIRYDATE","1.0"));
        children.add(createElement("DS_MERCHANT_CVV2","1.0"));
        return outputter.outputString(document);
    }


    /**
     * @param order
     * @return
     */

    public Map<String,String> buildPaymentParams(Order order) throws Exception {
        String transactionId = sequenceGenerator.getNextSequence();
        Map<String,String> params = new HashMap<String, String>();
        params.put("Ds_Action", virtualPosRequestUrl);
        params.put("Ds_Merchant_Amount", NumberUtil.formatForCardPayment(order.getTotalCost()));
        params.put("Ds_Merchant_Currency", currencyCode);
        params.put("Ds_Merchant_Order", transactionId);
        params.put("Ds_Merchant_ProductDescription","Llamar Y Comer");
        params.put("Ds_Merchant_MerchantURL", baseUrl + "/paymentResponse.html");
        params.put("Ds_Merchant_UrlOK", paymentBaseUrl + "/paymentAccepted.html");
        params.put("Ds_Merchant_UrlKO", paymentBaseUrl + "/paymentRejected.html");
        params.put("Ds_Merchant_MerchantData", order.getOrderId());
        params.put("Ds_Merchant_CardHolder",order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        params.put("Ds_Merchant_MerchantCode",merchantCode);
        params.put("Ds_Merchant_ConsumerLanguage",customerLanguage);
        params.put("Ds_Merchant_Terminal",terminalNumber);
        params.put("Ds_Merchant_TransactionType","0");
        params.put("Ds_Merchant_MerchantSignature",buildSignature(order, transactionId, 0));
        return params;
    }


    /**
     * @param name
     * @param value
     * @return
     */

    private Element createElement(String name, String value ) {
        Element element = new Element(name);
        element.setText(value);
        return element;
    }


    /**
     * @param order
     * @return
     */

    private HttpPost buildRequest( Order order ) throws Exception {
        String transactionId = sequenceGenerator.getNextSequence();
        HttpPost request = new HttpPost(virtualPosRequestUrl);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_Amount", NumberUtil.formatForCardPayment(order.getTotalCost())));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_Currency", currencyCode));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_Order", transactionId));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_ProductDescription","Online food order"));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_CardHolder",order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_MerchantCode",merchantCode));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_ConsumerLanguage",customerLanguage));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_Terminal",terminalNumber));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_TransactionType","0"));
        nameValuePairs.add(new BasicNameValuePair("Ds_Merchant_MerchantSignature",buildSignature(order,transactionId,0)));
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return request;
    }


    /**
     * @param order
     * @param transactionId
     * @return
     */

    private String buildSignature(Order order, String transactionId, int transactionType) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(NumberUtil.formatForCardPayment(order.getTotalCost()));
        sb.append(transactionId);
        sb.append(merchantCode);
        sb.append(currencyCode);
        sb.append(transactionType);
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
    @Value(value="${baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Required
    @Value(value="${paymentBaseUrl}")
    public void setPaymentBaseUrl(String paymentBaseUrl) {
        this.paymentBaseUrl = paymentBaseUrl;
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
