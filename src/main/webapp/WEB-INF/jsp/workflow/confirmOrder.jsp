<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.order-confirmation"/></title>
</head>

<body>

<h1>Thank you for your reponse</h1>
<h3>Order ${order.orderId} has status ${order.orderStatus}</h3>

</body>
