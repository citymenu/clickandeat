<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<script type="text/javascript">
    var orderid = '${orderid}';
</script>

<div class="order">
    <div class="orderdelivery"></div>
    <div class="orderitems">
        <table width="100%">
            <thead>
                <tr>
                    <th width="65%" align="left"><spring:message code="label.item"/></th>
                    <th width="10%" align="center"><spring:message code="label.quantity"/></th>
                    <th width="15%" align="center"><spring:message code="label.price"/></th>
                    <th width="10%"></th>
                </tr>
            </thead>
            <tbody class="orderbody">
                <tr class='ordertotal' valign='top'>
                    <td colspan=2 width="75%"><spring:message code="label.totalprice"/></td>
                    <td width="15%" align="center"><div class="totalcost"><span class="totalitemcost"></span></div></td>
                    <td width="10%"></td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="deliverycheck"></div>
    <div class="checkout"></div>
</div>
