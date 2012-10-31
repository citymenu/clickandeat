package com.ezar.clickandeat.payment;


import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.util.NumberUtil;
import com.ezar.clickandeat.util.SequenceGenerator;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.*;

@Component(value="paymentService")
public class PaymentService {

    private static final Logger LOGGER = Logger.getLogger(PaymentService.class);

    public static final String PRE_AUTHORIZE = "1";
    public static final String REVERSE = "9";
    public static final String CAPTURE = "2";
    public static final String REFUND = "3";

    private static final String RESPONSE_OK = "0";
    
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
        formatter.setOmitEncoding(false);
        formatter.setLineSeparator(LineSeparator.NONE);
        outputter = new XMLOutputter(formatter);
    }


    /**
     * @param order
     * @return
     */

    public Map<String,String> buildPaymentForm(Order order) throws Exception {
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
        params.put("Ds_Merchant_TransactionType",PRE_AUTHORIZE);
        params.put("Ds_Merchant_MerchantSignature",buildSignature(order, transactionId, PRE_AUTHORIZE));
        return params;
    }


    /**
     * @param order
     * @return
     * @throws Exception
     */

    public void processTransactionRequest(Order order, String transactionType) throws Exception {

        // Build XML request
        Document document = new Document(new Element("DATOSENTRADA"));
        Element root = document.getRootElement();
        List<Element> children = root.getChildren();
        children.add(createElement("DS_Version","0.1"));
        children.add(createElement("DS_MERCHANT_AMOUNT",NumberUtil.formatForCardPayment(order.getTotalCost())));
        children.add(createElement("DS_MERCHANT_CURRENCY",currencyCode));
        children.add(createElement("DS_MERCHANT_ORDER",order.getTransactionId()));
        children.add(createElement("DS_MERCHANT_MERCHANTCODE",merchantCode));
        children.add(createElement("DS_MERCHANT_MERCHANTSIGNATURE",buildSignature(order, order.getTransactionId(), transactionType)));
        children.add(createElement("DS_MERCHANT_TERMINAL",terminalNumber));
        children.add(createElement("DS_MERCHANT_TRANSACTIONTYPE",transactionType + ""));
        String xml = outputter.outputString(document);
        LOGGER.info("Built request: " + xml);

        // Post request to gateway
        HttpPost post = new HttpPost(virtualPosUrl);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("entrada",xml));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpClient client = new DefaultHttpClient();

        // Process response
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        // Build response xml
        SAXBuilder builder = new SAXBuilder();
        Document responseDocument = builder.build(new ByteArrayInputStream(sb.toString().getBytes("utf-8")));
        Element responseRoot = responseDocument.getRootElement();
        Element responseCodeElement = responseRoot.getChild("CODIGO");
        String responseCode = responseCodeElement.getText();
        
        if( !RESPONSE_OK.equals(responseCode) ) {
            String errorMessage = "Received response code: " + responseCode + " for transaction type " + transactionType + " for order id " + order.getOrderId();
            throw new Exception(errorMessage); 
        }
        
        LOGGER.info("Successfully processed transction type " + transactionType + " for order id " + order.getOrderId());
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
     * @param transactionId
     * @return
     */

    private String buildSignature(Order order, String transactionId, String transactionType) throws Exception {
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
