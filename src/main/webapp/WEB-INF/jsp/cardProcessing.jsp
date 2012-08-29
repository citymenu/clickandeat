<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.register"/></title>
    <script type="text/javascript" src="${resources}/script/cardProcessing.js"></script>
</head>

<body>

<form id="cardProcessingForm" method="post" action="${Ds_Action}">
    <input type="hidden" name="Ds_Merchant_Amount" value="${Ds_Merchant_Amount}"/>
    <input type="hidden" name="Ds_Merchant_Currency" value="${Ds_Merchant_Currency}"/>
    <input type="hidden" name="Ds_Merchant_Order" value="${Ds_Merchant_Order}"/>
    <input type="hidden" name="Ds_Merchant_ProductDescription" value="${Ds_Merchant_ProductDescription}"/>
    <input type="hidden" name="Ds_Merchant_CardHolder" value="${Ds_Merchant_CardHolder}"/>
    <input type="hidden" name="Ds_Merchant_MerchantCode" value="${Ds_Merchant_MerchantCode}"/>
    <input type="hidden" name="Ds_Merchant_ConsumerLanguage" value="${Ds_Merchant_ConsumerLanguage}"/>
    <input type="hidden" name="Ds_Merchant_Terminal" value="${Ds_Merchant_Terminal}"/>
    <input type="hidden" name="Ds_Merchant_TransactionType" value="${Ds_Merchant_TransactionType}"/>
    <input type="hidden" name="Ds_Merchant_MerchantSignature" value="${Ds_Merchant_MerchantSignature}"/>
</form>

</body>