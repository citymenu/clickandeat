package com.ezar.clickandeat.payment;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.util.NumberUtil;
import com.ezar.clickandeat.util.StringUtil;
import com.sun.javaws.jnl.XMLFormat;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;
import java.util.logging.XMLFormatter;

@Component(value = "cardPaymentService")
public class CardPaymentService {

    private static final Logger LOGGER = Logger.getLogger(CardPaymentService.class);

    private XMLOutputter outputter;

    private String virtualPosRequestUrl;
    
    private String virtualPosUrl;

    private String baseUrl;

    private String currencyCode;

    private String merchantCode;
    
    private String merchantName;

    private String terminalNumber;
    
    private String customerLanguage;

    private String secretCode;

    
    public CardPaymentService() {
        Format formatter = Format.getCompactFormat();
        formatter.setExpandEmptyElements(true);
        formatter.setOmitDeclaration(true);
        formatter.setOmitEncoding(true);
        outputter = new XMLOutputter(formatter);
    }


    /**
     * @param order
     */
    
    public void submitTransactionRequest(Order order) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(virtualPosRequestUrl);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("entrada",buildTransactionXml(order)));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
     * @throws Exception
     */

    private String buildTransactionXml(Order order) throws Exception {
        Document document = new Document(new Element("DATOSENTRADA"));
        Element root = document.getRootElement();
        List<Element> children = root.getChildren();
        children.add(createElement("DS_Version","1.0"));
        children.add(createElement("DS_MERCHANT_AMOUNT",NumberUtil.formatForCardPayment(order.getTotalCost())));
        children.add(createElement("DS_MERCHANT_CURRENCY",currencyCode));
        children.add(createElement("DS_MERCHANT_ORDER",order.getOrderId()));
        children.add(createElement("DS_MERCHANT_MERCHANTCODE",merchantCode));
        children.add(createElement("DS_MERCHANT_MERCHANTNAME",merchantName));
        children.add(createElement("DS_MERCHANT_CONSUMERLANGUAGE",customerLanguage));
        children.add(createElement("DS_MERCHANT_MERCHANTSIGNATURE",buildSignature(order)));
        children.add(createElement("DS_MERCHANT_TERMINAL",terminalNumber));
        children.add(createElement("DS_MERCHANT_TRANSACTIONTYPE","1"));
        children.add(createElement("DS_MERCHANT_PAN","4548812049400004"));
        children.add(createElement("DS_MERCHANT_EXPIRYDATE","1212"));
        children.add(createElement("DS_MERCHANT_CVV2","123"));
        return outputter.outputString(document);
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

    private String buildSignature(Order order) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(NumberUtil.formatForCardPayment(order.getTotalCost()));
        sb.append(order.getOrderId());
        sb.append(merchantCode);
        sb.append(currencyCode);
        sb.append("4548812049400004");
        sb.append("123");
        sb.append("1");
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
