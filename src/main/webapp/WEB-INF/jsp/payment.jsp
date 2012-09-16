<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/restaurant.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <script type="text/javascript" src="${resources}/script/payment.js"></script>
    <title><spring:message code="label.checkout"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <div class="content-left">
            <div id="paymentbody">
                <div id="paymentdetails">
                    <div class="checkoutheader"><spring:message code="label.card-details"/></div>
                    <div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.card-type"/></div>
                            <div class="detailsfield">
                                <select name="cardType" class="checkoutfield">
                                    <option value="VISA">VISA</option>
                                </select>
                            </div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.card-holders-name"/></div>
                            <div class="detailsfield"><input type="text" name="cardHoldersName" class="checkoutfield"/></div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.card-number"/></div>
                            <div class="detailsfield"><input type="text" name="cardNumber" class="checkoutfield"/></div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.issue-number"/></div>
                            <div class="detailsfield"><input type="text" name="issueNumber" class="checkoutfield" value="${order.customer.email}"/></div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.expiry-date"/></div>
                            <div class="detailsfield">
                                <select name="expiryMonth" class="checkoutfield">
                                    <option value=""><spring:message code="label.month-upper"/></option>
                                    <option value="1">01</option>
                                    <option value="2">02</option>
                                    <option value="3">03</option>
                                    <option value="4">04</option>
                                    <option value="5">05</option>
                                    <option value="6">06</option>
                                    <option value="7">07</option>
                                    <option value="8">08</option>
                                    <option value="9">09</option>
                                    <option value="10">10</option>
                                    <option value="11">11</option>
                                    <option value="12">12</option>
                                </select>
                                <select name="expiryYear" class="checkoutfield">
                                    <option value=""><spring:message code="label.year-upper"/></option>
                                    <option value="2012">2012</option>
                                    <option value="2013">2013</option>
                                    <option value="2014">2014</option>
                                    <option value="2015">2015</option>
                                    <option value="2016">2016</option>
                                    <option value="2017">2017</option>
                                    <option value="2018">2018</option>
                                </select>
                            </div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.security-code"/></div>
                            <div class="detailsfield"><input type="text" name="securityCode" class="checkoutfield"/></div>
                        </div>
                    </div>
                </div>

                <div>
                    <a href="javascript:deliveryOptions();" class="menuitem-button add-button unselectable">Delivery options</a>
                    <a href="javascript:placeOrder();" class="menuitem-button add-button unselectable">Place order</a>
                </div>

            </div>
        </div>
        <div class="content-right">
            <%@ include file="/WEB-INF/jsp/order.jsp" %>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>


</body>
</html>
