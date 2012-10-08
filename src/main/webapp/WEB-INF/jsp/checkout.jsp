<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&language=<locale:language/>&sensor=false"></script>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/checkout.css"/>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <script type="text/javascript" src="${resources}/script/validation.js"></script>
    <script type="text/javascript" src="${resources}/script/validation/validators_${validatorLocale}.js"></script>
    <script type="text/javascript" src="${resources}/script/checkout.js"></script>

    <!-- Google maps api -->

    <title><message:message key="page-title.checkout" escape="false"/></title>
    <script type="text/javascript">
        var deliveryType='${order.deliveryType}';
        var canCheckout=${order.canCheckout};
        var coordinates=[${order.restaurant.coordinates}];
    </script>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <td width="760">
                    <div class="checkout-left">
                        <div class="checkout-wrapper">
                            <!-- Customer details -->
                            <div class="checkout-item-wrapper">
                                <h2><message:message key="checkout.your-details"/></h2>
                                <div class="contact-form-table">
                                    <div class="contact-form-entry">
                                        <table width="630">
                                            <tr valign="top">
                                                <td width="130"><div class="contact-form-label"><message:message key="user.first-name"/>:<span class="required">*</span></div></td>
                                                <td width="470"><div class="contact-form-field"><input type="text" id="firstName" value="${order.customer.firstName}"/></div></td>
                                                <td width="30"><span id="firstName-validation" class="invalid"/></td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="contact-form-entry">
                                        <table width="630">
                                            <tr valign="top">
                                                <td width="130"><div class="contact-form-label"><message:message key="user.last-name"/>:<span class="required">*</span></div></td>
                                                <td width="470"><div class="contact-form-field"><input type="text" id="lastName" value="${order.customer.lastName}"/></div></td>
                                                <td width="30"><span id="lastName-validation" class="invalid"/></td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="contact-form-entry">
                                        <table width="630">
                                            <tr valign="top">
                                                <td width="130"><div class="contact-form-label"><message:message key="user.contact-number"/>:<span class="required">*</span></div></td>
                                                <td width="470">
                                                    <div class="contact-form-field"><input type="text" id="telephone" value="${order.customer.telephone}"/></div>
                                                    <div class="telephone-instructions"><message:message key="checkout.mobile-instructions" format="${order.restaurant.name}"/></div>
                                                </td>
                                                <td width="30"><span id="telephone-validation" class="invalid"/></td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="contact-form-entry">
                                        <table width="630">
                                            <tr valign="top">
                                                <td width="130"><div class="contact-form-label"><message:message key="user.email-address"/>:<span class="required">*</span></div></td>
                                                <td width="470"><div class="contact-form-field"><input type="text" id="email" value="${order.customer.email}"/></div></td>
                                                <td width="30"><span id="email-validation" class="invalid"/></td>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                            </div>

                            <!-- Delivery details -->
                            <div id="delivery-details" class="checkout-item-wrapper hidden">
                                <h2><message:message key="checkout.delivery-details"/></h2>
                                <div class="contact-form-table">
                                    <div class="contact-form-entry">
                                        <table width="630">
                                            <tr valign="top">
                                                <td width="130"><div class="contact-form-label"><message:message key="user.street-address"/>:<span class="required">*</span></div></td>
                                                <td width="470"><div class="contact-form-field"><input type="text" id="address1" value="${order.deliveryAddress.address1}"/></div></td>
                                                <td width="30"><span id="address1-validation" class="invalid"/></td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="contact-form-entry">
                                        <table width="630">
                                            <tr valign="top">
                                                <td width="130"><div class="contact-form-label"><message:message key="user.town"/>:</div></td>
                                                <td width="470"><div class="contact-form-field"><input type="text" id="town" value="${order.deliveryAddress.town}"/></div></td>
                                                <td width="30"></td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="contact-form-entry">
                                        <table width="630">
                                            <tr valign="top">
                                                <td width="130"><div class="contact-form-label"><message:message key="user.region"/>:</div></td>
                                                <td width="470"><div class="contact-form-field"><input type="text" id="region" value="${order.deliveryAddress.region}"/></div></td>
                                                <td width="30"></td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="contact-form-entry">
                                        <table width="630">
                                            <tr valign="top">
                                                <td width="130"><div class="contact-form-label"><message:message key="user.post-code"/>:<span class="required">*</span></div></td>
                                                <td width="470"><div class="contact-form-field"><input type="text" id="postCode" value="${order.deliveryAddress.postCode}"/></div></td>
                                                <td width="30"><span id="postCode-validation" class="invalid"/></td>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                            </div>

                            <!-- Collection details -->
                            <div id="collection-details" class="checkout-item-wrapper hidden">
                                <h2><message:message key="checkout.collection-details"/></h2>
                                <div class="checkout-description"><message:message key="checkout.collection-help" format="${order.restaurant.name}"/></div>
                                <div id="restaurant-location"></div>
                            </div>

                            <!-- Additional instructions -->
                            <div class="checkout-item-wrapper">
                                <h2><message:message key="checkout.additional-instructions"/></h2>
                                <div class="checkout-description"><message:message key="checkout.additional-instructions-help" format="${order.restaurant.name}"/></div>
                                <div class="contact-form-entry">
                                    <div class="contact-form-field">
                                        <textarea id="additionalInstructions">${order.additionalInstructions}</textarea>
                                    </div>
                                </div>
                            </div>

                            <!-- Vouchers -->
                            <div class="checkout-item-wrapper">
                                <h2><message:message key="checkout.apply-voucher"/></h2>
                                <div class="checkout-description"><message:message key="checkout.vouchers-help"/></div>
                                <div class="contact-form-entry">
                                    <div class="contact-form-field">
                                        <input type="text" id="voucherid" style="width:130px; margin-right:10px;"/>
                                        <a class="checkout-nav-button checkout-nav-button-large" onclick="applyVoucher()"><message:message key="button.apply"/></a>
                                    </div>
                                </div>
                            </div>

                            <!-- Navigation buttons -->
                            <div class="checkout-navigation-wrapper">
                                <a class="checkout-nav-button checkout-nav-button-large" onclick="updateOrder()"><message:message key="button.update-order"/></a>
                                <a class="checkout-nav-button checkout-nav-button-large" onclick="proceedToPayment()"><message:message key="button.payment"/></a>
                            </div>
                        </div>
                    </div>
                </td>
                <td width="260">
                    <div class="checkout-right">
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
