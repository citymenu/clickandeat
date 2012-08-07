<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.checkout"/></title>
    <script type="text/javascript" src="${resources}/script/tools.js"></script>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <script type="text/javascript" src="${resources}/script/checkout.js"></script>
</head>

<body>

<script type="text/javascript">
    var deliveryType='${deliveryType}';
</script>

<div id="maincontent">
    <%@ include file="/WEB-INF/jsp/workflow.jsp" %>
    <div id="contentbody">
        <div id="checkoutbody">
            <div id="contactdetails">
                <div class="checkoutheader"><spring:message code="label.your-details"/></div>
                <div>
                    <div class="detailsitem">
                        <div class="detailslabel"><spring:message code="label.first-name"/></div>
                        <div class="detailsfield"><input type="text" name="firstName" class="checkoutfield" value="${order.customer.firstName}"/></div>
                    </div>
                    <div class="detailsitem">
                        <div class="detailslabel"><spring:message code="label.last-name"/></div>
                        <div class="detailsfield"><input type="text" name="lastName" class="checkoutfield" value="${order.customer.lastName}"/></div>
                    </div>
                    <div class="detailsitem">
                        <div class="detailslabel"><spring:message code="label.contact-number"/></div>
                        <div class="detailsfield"><input type="text" name="telephone" class="checkoutfield" value="${order.customer.telephone}"/></div>
                    </div>
                    <div class="detailsitem">
                        <div class="detailslabel"><spring:message code="label.email-address"/></div>
                        <div class="detailsfield"><input type="text" name="email" class="checkoutfield" value="${order.customer.email}"/></div>
                    </div>
                </div>

                <div id="additionalinstructions">
                    <div class="checkoutheader"><spring:message code="label.additionalinstructions"/></div>
                    <div>
                        <div class="checkoutsummary"><spring:message code="label.additional-instructions-text"/></div>
                        <div class="additionalinstructionsfield">
                            <textarea name="additionalInstructions" class="checkoutfield">${order.additionalInstructions}</textarea>
                        </div>
                    </div>
                </div>

                <div id="deliverytime">
                    <div class="checkoutheader"><spring:message code="label.delivery-time"/></div>
                    <div>
                        <div class="checkoutsummary"><spring:message code="label.delivery-time-text"/></div>
                        <div class="checkouttime">
                            <select name="requestedTime" id="requestedDeliveryTime" class="checkoutselect">
                                <option value="asap"><spring:message code="label.asap"/></option>
                            </select>
                        </div>
                    </div>

                    <div class="checkoutheader"><spring:message code="label.delivery-details"/></div>
                    <div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.address"/></div>
                            <div class="detailsfield"><input type="text" name="address1" class="checkoutfield" value="${order.deliveryAddress.address1}"/></div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel">&nbsp;</div>
                            <div class="detailsfield"><input type="text" name="address2" class="checkoutfield" value="${order.deliveryAddress.address2}"/></div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel">&nbsp;</div>
                            <div class="detailsfield"><input type="text" name="address3" class="checkoutfield" value="${order.deliveryAddress.address3}"/></div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.town"/></div>
                            <div class="detailsfield"><input type="text" name="town" class="checkoutfield" value="${order.deliveryAddress.town}"/></div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.region"/></div>
                            <div class="detailsfield"><input type="text" name="region" class="checkoutfield" value="${order.deliveryAddress.region}"/></div>
                        </div>
                        <div class="detailsitem">
                            <div class="detailslabel"><spring:message code="label.postcode"/></div>
                            <div class="detailsfield"><input type="text" name="postCode" class="checkoutfield" value="${order.deliveryAddress.postCode}"/></div>
                        </div>
                    </div>
                </div>

                <div id="collectiontime">
                    <div class="checkoutheader"><spring:message code="label.collection-time"/></div>
                    <div>
                        <div class="checkoutsummary"><spring:message code="label.collection-time-text"/></div>
                        <div class="checkouttime">
                            <select name="requestedTime" id="requestedCollectionTime" class="checkoutselect">
                                <option value="asap"><spring:message code="label.asap"/></option>
                            </select>
                        </div>
                    </div>
                </div>

            </div>

            <div>
                <input type="button" id="updateorderbutton" value="<spring:message code="label.update-order"/>"/>
                <input type="button" id="proceedbutton" value="<spring:message code="label.proceed"/>"/>
            </div>

        </div>
    </div>
</div>

<div id="rightbar">
    <%@ include file="/WEB-INF/jsp/order.jsp" %>
</div>


</body>
</html>
