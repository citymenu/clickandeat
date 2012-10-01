<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<script type="text/javascript">
    var orderid = '${orderid}';
</script>

<div id="order-wrapper">
    <div class="outer-box">
        <div class="middle-box">
            <div class="center-box">
                <div class='orderheader'><message:message key="order.your-order"/>:</div>
                <div id="restaurant-warning-wrapper"></div>
                <div class="order-delivery-wrapper"></div>
                <div id="deliverycheck"></div>
                <div class="order-items-wrapper">
                    <div class="order-item-wrapper-header">
                        <table width="194">
                            <tr>
                                <td width="124" align="left"><h3 class="order-table"><message:message key="order.item"/></h3></td>
                                <td width="50" align="center"><h3 class="order-table"><message:message key="order.price"/></h3></td>
                                <td width="20"></td>
                            </tr>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                    <div id="order-item-contents"></div>
                </div>
                <div id="additionalinstructions"></div>
                <div id="discounts"></div>
                <div id="ordertotal"><message:message key="order.total"/>:</div>
                <div id="checkoutcontainer"></div>
            </div>
        </div>
    </div>
</div>
