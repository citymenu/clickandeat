<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <script type="text/javascript" src="${resources}/script/payment.js"></script>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/payment.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>

    <title><message:message key="page-title.payment" escape="false"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <!-- Payment form -->
                <td width="760">
                    <div class="payment-wrapper">
                        <div class="payment-title-wrapper">
                            <div class="payment-title"><h2><message:message key="payment.payment-details"/></h2></div>
                            <c:if test="${error != null}">
                                <div class="payment-error"><util:escape value="${error}" escapeNewLines="true"/></div>
                            </c:if>
                        </div>
                        <div id="paymentbody">
                            <iframe name="paymentForm" src="${ctx}/cardProcessing.html" frameborder="0" border="0" cellspacing="0" style="border-style: none;width: 100%; height: 100%;"></iframe>
                        </div>
                    </div>
                </td>

                <!-- Order panel -->
                <td width="260">
                    <div class="menu-right">
                        <%@ include file="/WEB-INF/jsp/order.jsp" %>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>


</body>
</html>
