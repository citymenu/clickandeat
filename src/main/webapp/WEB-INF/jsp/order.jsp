<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<script type="text/javascript">
    var orderid = '${orderid}';
</script>

<div id="ordercontainer">
    <div id="order">
        <div class='orderheader'><spring:message code="label.your-order"/>:</div>
        <div class="orderdelivery"></div>
        <div class="orderitems">
            <table width="100%" class="ordertable">
                <thead>
                    <tr>
                        <th width="65%" align="left" class="ordertableheader ordertableseparator"><spring:message code="label.item"/></th>
                        <th width="25%" align="center" class="ordertableheader ordertableseparator"><spring:message code="label.price"/></th>
                        <th width="10%" class="ordertableheader"></th>
                    </tr>
                </thead>
                <tbody class="orderbody">
                </tbody>
            </table>
        </div>
        <div id="ordertotal"><spring:message code="label.order-total"/>:</div>
        <div class="deliverycheck"></div>
        <div id="checkoutcontainer"></div>
    </div>
</div>
