<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/callnow.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/ordersummary.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css" charset="utf-8"/>

    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/script/orders.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/validation.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/validation/validators_${systemLocale}.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/orders.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/callnowsummary.js" charset="utf-8"></script>

    <title>LlamaryComer | <message:message key="page-title.call-now-order" escape="false"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<script type="text/javascript">
    var orderid = '${order.orderId}';
    var coordinates=[${order.restaurant.coordinates}];
</script>

<div id="content">

    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <!-- Order summary -->
                <td width="760">
                    <div class="order-summary-wrapper">
                        <h2>Gracias por usar <message:message key="title.companyname" escape="false"/></h2>
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
                                                    <div class="restaurant-contact">N&#250;mero de teléfono: ${order.restaurant.contactTelephone}</div>
                                                </div>

                                                <div id="notregistered">
                                                    <div class="order-overview">
                                                        <h2>¿Y ahora qué?</h2>
                                                        <p>El restaurante <util:escape value="${order.restaurant.name}"/> no acepta pagos on-line. Para solicitar tu pedido llama al <util:escape value="${order.restaurant.contactTelephone}"/> ahora mismo.</p>
                                                        <p>En estos momentos <message:message key="title.companyname" escape="false"/> esta ofreciendo una promoci&#243;n cuando completas tu pedido telef&#243;nico.</p>
                                                        <p>Introduce tu correo electr&#243;nico y pincha en el bot&#243;n <message:message key="button.call-now.send.voucher"/>. Una vez hayamos confirmado tu pedido con <util:escape value="${order.restaurant.name}"/> te enviaremos un correo electr&#243;nico con tu cup&#243;n de descuento.</p>
                                                        <p>No te olvides que para recibir tu cup&#243;n de descuento debes mencionar el nombre <message:message key="title.companyname" escape="false"/> y el n&#250;mero de pedido cuando hables con <util:escape value="${order.restaurant.name}"/>.<p/>.
                                                    </div>

                                                    <div class="email-entry">
                                                        <input type="text" id="email" style="width:200px; margin-right:10px;"/>
                                                        <a class="call-now-nav-button" id="#register" onclick="register()"><message:message key="button.call-now.send.voucher"/></a>
                                                        <div class="invalid-email">Por favor introduce un correo electr&#243;nico v&#225;lido</div>
                                                    </div>
                                                </div>
                                                <div id="registered">
                                                    <div class="order-overview">
                                                        <h2>Gracias por registrarte con nosotros</h2>
                                                        <p>Una vez hayamos confirmado tu pedido con <util:escape value="${order.restaurant.name}"/> te enviaremos un correo electr&#243;nico con tu cup&#243;n de descuento
                                                        para que lo uses con tu pr&#243;ximo pedido online</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <!-- Explanation -->
                                    <td width="290">
                                        <div class="call-now-image"></div>
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
