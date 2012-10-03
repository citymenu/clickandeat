<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/ordersummary.css"/>
    <title><message:message key="page-title.order-confirmation" escape="false"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>



<div id="content">
    <div class="content-wrapper">
        <div class="order-summary-wrapper">
            <h2><message:message key="order.thankyou-for-your-order"/></h2>
            <div><util:escape value="${order.restaurant.name}" escapeComments="true"/></div>
            <div><util:escape value="${order.restaurant.address.summary}" escapeComments="true"/></div>

            <div><message:message key="order.order-number"/>: ${order.orderId}</div>

            <div>
                <c:choose>
                    <c:when test="${order.deliveryType == 'DELIVERY'}">
                        <message:message key="order.for-delivery"/>:<util:escape value="${order.expectedDeliveryTimeString}"/>
                    </c:when>
                    <c:otherwise>
                        <message:message key="order.for-collection"/>:<util:escape value="${order.expectedCollectionTimeString}"/>
                    </c:otherwise>
                </c:choose>
            </div>

            <div><message:message key="order.awaiting-restaurant-instructions" format="${order.restaurant.name}"/></div>

            <div><message:message key="order.order-items"/></div>
            <div>
                <table width="400">
                    <tr valign="top">
                        <td width="320"><message:message key="order.item"/></td>
                        <td width="80" align="center"><message:message key="order.cost"/></td>
                    </tr>
                    <c:forEach var="orderItem" items="${order.orderItems}">
                    <tr valign="top">
                        <td width="320"><util:escape value="${orderItem}" escapeComments="true" escapeNewLines="true"/></td>
                        <td width="80" align="right"><message:message key="config.currency" escape="false"/>${orderItem.formattedCost}</td>
                    </tr>
                    </c:forEach>
                    <c:forEach var="orderDiscount" items="${order.orderDiscounts}">
                        <c:if test="${!(orderDiscount.discountType == 'DISCOUNT_FREE_ITEM' && orderDiscount.selectedFreeItem == '')}">
                        <tr valign="top">
                        <c:choose>
                            <c:when test="${orderDiscount.discountType == 'DISCOUNT_FREE_ITEM'}">
                                <td width="320"><util:escape value="${orderDiscount.selectedFreeItem}"/></td>
                                <td width="80" align="right"><message:message key="config.currency" escape="false"/>0.00</td>
                            </c:when>
                            <c:otherwise>
                                <td width="320"><util:escape value="${orderDiscount.title}"/></td>
                                <td width="80" align="right">-<message:message key="config.currency" escape="false"/>{orderDiscount.formattedAmount}</td>
                            </c:otherwise>
                        </c:choose>
                        </tr>
                        </c:if>
                    </c:forEach>
                    <c:if test="${order.deliveryCost != null && order.deliveryCost > 0}">
                    <tr valign="top">
                        <td width="320"><message:message key="order.delivery-charge"/></td>
                        <td width="80" align="right"><message:message key="config.currency" escape="false"/>${order.formattedDeliveryCost}</td>
                    </tr>
                    </c:if>
                    <tr valign="top">
                        <td width="320"><message:message key="order.total-cost"/></td>
                        <td width="80" align="right"><message:message key="config.currency" escape="false"/>${order.formattedTotalCost}</td>
                    </tr>
                </table>
            </div>

            <c:if test="${order.additionalInstructions != ''}">
                <div>
                    <div><message:message key="order.additional-instructions"/></div>
                    <div><util:escape value="${order.additionalInstructions}" escapeComments="true" escapeNewLines="true"/></div>
                </div>
            </c:if>

        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>

</body>
</html>
