<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <script type="text/javascript" src="${resources}/script/cardProcessing.js" charset="utf-8"></script>
</head>

<body>

<form id="cardProcessingForm" method="post" action="${Ds_Action}">
    <input type="hidden" name="Ds_Merchant_Amount" value="${Ds_Merchant_Amount}"/>
    <input type="hidden" name="Ds_Merchant_Currency" value="${Ds_Merchant_Currency}"/>
    <input type="hidden" name="Ds_Merchant_Order" value="${Ds_Merchant_Order}"/>
    <input type="hidden" name="Ds_Merchant_ProductDescription" value="${Ds_Merchant_ProductDescription}"/>
    <input type="hidden" name="Ds_Merchant_UrlOK" value="${Ds_Merchant_UrlOK}"/>
    <input type="hidden" name="Ds_Merchant_UrlKO" value="${Ds_Merchant_UrlKO}"/>
    <input type="hidden" name="Ds_Merchant_MerchantData" value="${Ds_Merchant_MerchantData}"/>
    <input type="hidden" name="Ds_Merchant_CardHolder" value="${Ds_Merchant_CardHolder}"/>
    <input type="hidden" name="Ds_Merchant_MerchantCode" value="${Ds_Merchant_MerchantCode}"/>
    <input type="hidden" name="Ds_Merchant_ConsumerLanguage" value="${Ds_Merchant_ConsumerLanguage}"/>
    <input type="hidden" name="Ds_Merchant_Terminal" value="${Ds_Merchant_Terminal}"/>
    <input type="hidden" name="Ds_Merchant_TransactionType" value="${Ds_Merchant_TransactionType}"/>
    <input type="hidden" name="Ds_Merchant_MerchantSignature" value="${Ds_Merchant_MerchantSignature}"/>
</form>

</body>