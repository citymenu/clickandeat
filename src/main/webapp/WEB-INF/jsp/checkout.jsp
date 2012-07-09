<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.checkout"/></title>
    <script type="text/javascript" src="${ctx}/resources/script/tools.js"></script>
    <script type="text/javascript" src="${ctx}/resources/script/checkout.js"></script>
    <script type="text/javascript" src="${ctx}/resources/script/orders.js"></script>
</head>

<body>

<div id="ordernav">
    <ul>
        <li><a href="${ctx}/search.html${search.queryString}">1) Find a Restaurant</a></li>
        <li><a href="${ctx}/buildOrder.html">2) Build Your Order</a></li>
        <li><a href="${ctx}/checkout.html">3) Checkout</a></li>
    </ul>
</div>

<%@ include file="/WEB-INF/jsp/order.jsp" %>

<div id="login" class="boxcontainer">
    <h3><spring:message code="label.signin"/></h3>
    <form id="signinForm" method="post" action="${ctx}/secure/signin.html">
        <div><spring:message code="label.emailaddress"/></div>
        <div><input type="text" id="email" class="required email"/></div>
        <div><spring:message code="label.password"/></div>
        <div><input type="password" id="password" class="required"/></div>
        <div><input id="signin" type="button" value="<spring:message code="label.signin"/>"/></div>
    </form>
</div>

<div id="orderwithoutlogin" class="boxcontainer">
    <h3><spring:message code="label.orderwithoutlogin"/></h3>
    <form id="proceedForm" method="post" action="${ctx}/secure/setcustomerdetails.ajax">
        <div><spring:message code="label.firstname"/></div>
        <div><input type="text" name="firstName" class="required"/></div>
        <div><spring:message code="label.lastname"/></div>
        <div><input type="text" name="lastName"/></div>
        <div><spring:message code="label.emailaddress"/></div>
        <div><input type="text" name="email"/></div>
        <div><spring:message code="label.confirmemailaddress"/></div>
        <div><input type="text" name="confirmEmail"/></div>
        <div><spring:message code="label.telephone"/></div>
        <div><input type="text" name="telephone"/></div>
        <div><spring:message code="label.mobile"/></div>
        <div><input type="text" name="mobile"/></div>
        <div><input id="proceed" type="button" value="<spring:message code="label.proceed"/>"/></div>
    </form>
</div>

</body>
</html>
