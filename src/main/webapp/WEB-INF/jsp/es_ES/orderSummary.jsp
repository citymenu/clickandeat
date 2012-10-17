<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <script type="text/javascript" src="${resources}/script/ordersummary.js"></script>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/ordersummary.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>

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
                                                <div class="order-information">Numero de pedido: ${order.orderId}</div>
                                                <div class="order-restaurant">
                                                    <util:escape value="${order.restaurant.name}"/> <util:escape value="${order.restaurant.address.summary}"/>
                                                    <div class="restaurant-contact">Contacto: ${order.restaurant.notificationOptions.notificationPhoneNumber}</div>
                                                </div>
                                                <div class="order-overview">
                                                    <h2>¿Que ocurre a continuación?</h2>
                                                    <p>Acabamos de comunicar tu pedido a <util:escape value="${order.restaurant.name}"/>.
                                                    Pronto recibirás un correo electrónico confirmando que han recibido tu orden.</p>
                                                    <p>Si por algún motivo el restaurante no puede llevar a cabo tu pedido te lo haremos saber de manera inmediata.</p>
                                                    <p>Si tienes alguna pregunta relacionada con tu pedido, por favor contacta <util:escape value="${order.restaurant.name}"/> utilizando
                                                     el número de teléfono que aparece arriba y no te olvides de mencionar tu numero de pedido.</p>
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
                                        <div class="order-image">
                                            Put picture of butler in a chef hat here or something like that.
                                        </div>
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
                                                        80 Peel Road<br>
                                                        South Woodford<br>
                                                        London<br>
                                                        E18 2LG
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <h2>Dirección (recoger)</h2>
                                                    <p>Has solicitado recoger tu pedido en persona de <util:escape value="${order.restaurant.name}"/>.</p>
                                                    <p>Abajo esta la posición de <util:escape value="${order.restaurant.name}"/>.</p>
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

<%@ include file="/WEB-INF/jsp/footer.jsp" %>

</body>
</html>
