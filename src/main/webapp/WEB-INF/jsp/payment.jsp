<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.checkout"/></title>
    <script type="text/javascript" src="${ctx}/resources/script/tools.js"></script>
    <script type="text/javascript" src="${ctx}/resources/script/orders.js"></script>
    <script type="text/javascript" src="${ctx}/resources/script/payment.js"></script>
</head>

<body>

<div id="maincontent">
    <%@ include file="/WEB-INF/jsp/workflow.jsp" %>
    <div id="contentbody">
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
                <input type="button" id="deliveryoptionsbutton" value="<spring:message code="label.delivery-options"/>"/>
                <input type="button" id="placeorderbutton" value="<spring:message code="label.place-order"/>"/>
            </div>

        </div>
    </div>
</div>

<div id="rightbar">
    <%@ include file="/WEB-INF/jsp/order.jsp" %>
</div>


</body>
</html>
