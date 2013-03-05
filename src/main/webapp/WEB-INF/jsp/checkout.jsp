<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/speechbubble.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/checkout.css" charset="utf-8"/>

    <script type="text/javascript" src="${resources}/script/orders.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/validation.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/validation/validators_${systemLocale}.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/checkout.js" charset="utf-8"></script>

    <title><message:message key="page-title.checkout" escape="false"/></title>
    <script type="text/javascript">
        var deliveryType='${order.deliveryType}';
        var canCheckout=${order.canCheckout};
        var coordinates=[${order.restaurant.coordinates}];
    </script>
</head>

<body>

<div id="content">
    <div class="content-wrapper">
        <table width="939">
            <tr valign="top">
                <td width="679">
                    <div class="checkout-left">
                        <div class="checkout-wrapper">

                            <!-- Validation wrapper -->
                            <div id="validation-error" style="display:none;">
                                <div class="validation-message">
                                    <p class="triangle-isosceles left" id="validation-message-wrapper"></p>
                                </div>
                            </div>

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
                                                <td width="130"><div class="contact-form-label"><message:message key="user.last-name"/>:</div></td>
                                                <td width="470"><div class="contact-form-field"><input type="text" id="lastName" value="${order.customer.lastName}"/></div></td>
                                                <td width="30"></td>
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

                            <!-- Vouchers -->
                            <div class="checkout-item-wrapper">
                                <h2><message:message key="checkout.apply-voucher"/></h2>
                                <div class="checkout-description"><message:message key="checkout.vouchers-help"/></div>
                                <div class="contact-form-field">
                                    <input type="text" id="voucherid" style="width:130px; margin-right:10px;"/>
                                    <a class="checkout-nav-button checkout-nav-button-large" onclick="applyVoucher()"><message:message key="button.apply"/></a>
                                </div>
                            </div>

                            <!-- Terms and conditions -->
                            <div class="checkout-item-wrapper">
                                <h2><message:message key="checkout.terms-and-conditions"/></h2>
                                <div class="checkout-description"><message:message key="checkout.terms-and-conditions-help"/></div>
                                <div class="contact-form-field">
                                    <c:choose>
                                        <c:when test="${order.termsAndConditionsAccepted == true}">
                                            <input type="checkbox" id="termsAndConditions" checked="checked" style="margin-right:10px;"/>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="checkbox" id="termsAndConditions" style="margin-right:10px;"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="terms-and-conditions"><message:message key="checkout.accept-terms-and-conditions" escape="false"/></span>
                                </div>

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

                            <!-- Collection details -->
                            <div id="collection-details" class="checkout-item-wrapper hidden">
                                <h2><message:message key="checkout.collection-details"/></h2>
                                <div class="checkout-description"><message:message key="checkout.collection-help" format="${order.restaurant.name}"/></div>
                                <div id="restaurant-location"></div>
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

</body>
</html>
