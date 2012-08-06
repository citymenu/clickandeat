<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.complete"/></title>
</head>

<body>

<div id="maincontent">
    <%@ include file="/WEB-INF/jsp/workflow.jsp" %>
    <div id="contentbody">
        <div id="paymentbody">
            <div id="paymentdetails">
                <div class="checkoutheader"><spring:message code="label.thankyou-for-your-order"/> (${order.orderId})</div>
            </div>
        </div>
    </div>
</div>

<div id="rightbar"></div>


</body>
</html>
