<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<script type="text/javascript">
    var orderid = '${orderid}';
</script>

<div id="order-wrapper">
    <div class='orderheader'></div>
    <div id="location-wrapper"></div>
    <div class="order-delivery-wrapper"></div>
    <div class="order-items-wrapper">
        <div class="order-item-wrapper-header">
            <table width="206">
                <tr>
                    <td width="156" align="left"><h3 class="order-table"><message:message key="order.item"/></h3></td>
                    <td width="50" align="center"><h3 class="order-table"><message:message key="order.price"/></h3></td>
                </tr>
                <tbody>
                </tbody>
            </table>
        </div>
        <div id="order-item-contents"></div>
    </div>
    <div id="checkoutcontainer"></div>
    <div id="deliverycheck"></div>
    <div id="additionalinstructions"></div>
    <div id="discounts"></div>
</div>
