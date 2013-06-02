<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css" charset="utf-8">
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/callnow.css" charset="utf-8">
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/ordersummary.css" charset="utf-8">

    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&amp;language=<locale:language/>&amp;sensor=false"></script>
    <script type="text/javascript" src="${resources}/script/orders.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/validation.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/validation/validators_${systemLocale}.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/orders.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/callnowsummary.js" charset="utf-8"></script>

    <title>LlamaryComer | <message:message key="page-title.call-now-order" escape="false"/></title>
</head>

<body>

    <script type="text/javascript">
        var orderid = '${order.orderId}';
        var coordinates=[${order.restaurant.coordinates}];
    </script>

    <jsp:include page="/WEB-INF/jsp/header.jsp" />
    <div id="content">

        <div class="content-wrapper">
            <table width="1000">
                <tr valign="top">
                    <!-- Order summary -->
                    <td width="740">
                        <div class="order-summary-wrapper">
                            <h2>Thank you for using <message:message key="title.companyname" escape="false"/></h2>
                            <div class="order-detail-wrapper">
                                <table width="700">
                                    <tr valign="top">
                                        <!-- Order detalis -->
                                        <td width="430">
                                            <div class="order-overview-wrapper">
                                                <div class="order-detail">
                                                    <div class="order-information">Your order number is ${order.orderId}</div>
                                                    <div class="order-restaurant">
                                                        <util:escape value="${order.restaurant.name}"/> <util:escape value="${order.restaurant.address.summary}"/>
                                                        <div class="restaurant-contact">Contact: ${order.restaurant.contactTelephone}</div>
                                                    </div>
                                                    <div id="notregistered">
                                                        <div class="order-overview">
                                                            <h2>What happens next?</h2>
                                                            <p>${order.restaurant.name} does not take online payments yet but you can contact them on the number shown above right now.</p>
                                                            <p><message:message key="title.companyname" escape="false"/> is currently offering a discount voucher if you complete your telephone order.</p>
                                                            <p>Enter your email address in the field below and click on <message:message key="button.call-now.send.voucher"/> button. Once your order is confirmed with <util:escape value="${order.restaurant.name}"/> you will soon receive a discount voucher.</p>
                                                            <p>Do not forget that to receive your discount voucher you will have to mention the order id above to <util:escape value="${order.restaurant.name}"/> when you phone them.</p>
                                                        </div>

                                                        <div class="email-entry">
                                                            <input type="text" id="email" style="width:200px; margin-right:10px;"/>
                                                            <a class="call-now-nav-button" id="#register" onclick="register()"><message:message key="button.call-now.send.voucher"/></a>
                                                            <div class="invalid-email">Please enter a valid email address</div>
                                                        </div>
                                                    </div>
                                                    <div id="registered">
                                                        <div class="order-overview">
                                                            <h2>Thanks for registering with us</h2>
                                                            <p>As soon as we receive confirmation of your order from <util:escape value="${order.restaurant.name}"/> we will send you your discount voucher
                                                            entitling you to <b>10% off</b> your next online order with us.</p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <!-- Explanation -->
                                        <td width="270">
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

</body>
</html>
