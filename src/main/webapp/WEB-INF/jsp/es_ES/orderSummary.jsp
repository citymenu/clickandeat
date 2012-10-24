<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/script/orders.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/ordersummary.js" charset="utf-8"></script>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/ordersummary.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css" charset="utf-8"/>

    <title>LlamaryComer | <message:message key="page-title.order-confirmation" escape="false"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script type="text/javascript">
    var completedorderid = '${completedorderid}';
    var coordinates=[${order.restaurant.coordinates}];
</script>

<div id="content">

    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <!-- Order summary -->
                <td width="760">
                    <div class="order-summary-wrapper">
                        <h2>Gracias por tu pedido <util:escape value="${order.customer.firstName}"/></h2>
                        <div class="order-detail-wrapper">
                            <table width="720">
                                <tr valign="top">
                                    <!-- Order detalis -->
                                    <td width="430">
                                        <div class="order-overview-wrapper">
                                            <div class="order-detail">
                                                <div class="order-information">N&#250;mero de pedido: ${order.orderId}</div>
                                                <div class="order-restaurant">
                                                    <util:escape value="${order.restaurant.name}"/> <util:escape value="${order.restaurant.address.summary}"/>
                                                    <div class="restaurant-contact">Contacto: ${order.restaurant.notificationOptions.notificationPhoneNumber}</div>
                                                </div>
                                                <div class="order-overview">
                                                    <h2>&#191;Que ocurre a continuaci&#243;n?</h2>
                                                    <p>Acabamos de comunicar tu pedido a <util:escape value="${order.restaurant.name}"/>.
                                                    Pronto recibir&#225;s un correo electr&#243;nico confirmando que han recibido tu orden.</p>
                                                    <p>Si por alg&#250;n motivo el restaurante no puede llevar a cabo tu pedido te lo haremos saber de manera inmediata.</p>
                                                    <p>Si tienes alguna pregunta relacionada con tu pedido, por favor contacta <util:escape value="${order.restaurant.name}"/> utilizando
                                                    el n&#250;mero de tel&#233;fono que aparece arriba y no te olvides de mencionar tu numero de pedido.</p>
                                                    <div class="delivery-time">
                                                        <c:choose>
                                                            <c:when test="${order.deliveryType == 'DELIVERY'}">
                                                                Normalmente los pedido se entregan en ${order.restaurant.deliveryTimeMinutes} minutos.
                                                            </c:when>
                                                            <c:otherwise>
                                                                Normalmente los pedido los pedidos estan listos para recoger en ${order.restaurant.collectionTimeMinutes} minutos.
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <!-- Explanation -->
                                    <td width="290">
                                        <div class="order-image"></div>
                                    </td>
                                </tr>
                                <tr valign="top">
                                    <td width="720" colspan="2">
                                        <div class="order-delivery">
                                            <c:choose>
                                                <c:when test="${order.deliveryType == 'DELIVERY'}">
                                                    <h2>Dirección (enviar)</h2>
                                                    <p>Has solicitado que <util:escape value="${order.restaurant.name}"/> entregue el pedido en la dirección siguiente:</p>
                                                    <div class="delivery-address">
                                                        <util:escape value="${order.deliveryAddress.displaySummary}" escapeNewLines="true"/>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <h2>Dirección (recoger)</h2>
                                                    <p>Has solicitado recoger tu pedido en persona de <util:escape value="${order.restaurant.name}"/>.</p>
                                                    <p>Abajo esta la posici&#243;n de <util:escape value="${order.restaurant.name}"/>.</p>
                                                    <div id="restaurant-location"></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </table>
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

<jsp:include page="/WEB-INF/jsp/${systemLocale}/footer.jsp" />

</body>
</html>
