<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<script type="text/javascript">

    // Current order
    var orderid = '${orderid}';

    //Translations
    var labels = ({
        'delivery': "<spring:message code="label.delivery"/>",
        'collection': "<spring:message code="label.collection"/>",
        'delivery-charge': "<spring:message code="label.delivery-charge"/>",
        'collection-discount': "<spring:message code="label.collection-discount"/>",
        'delivery-warning': "<spring:message code="label.delivery-warning"/>",
        'checkout': "<spring:message code="label.checkout"/>",
        'restaurant-warning': "<spring:message code="label.restaurant-warning"/>",
        'are-you-sure': "<spring:message code="label.are-you-sure"/>",
        'add-item-anyway': "<spring:message code="label.add-item-anyway"/>",
        'dont-add-item': "<spring:message code="label.dont-add-item"/>",
        'remove-from-order': "<spring:message code="label.remove-from-order"/>",
        'update-order': "<spring:message code="label.update-order"/>",
        'no-thanks': "<spring:message code="label.no-thanks"/>",
        'free': "<spring:message code="label.free"/>",
        'choose-additional': "<spring:message code="label.choose-additional"/>",
        'additional-item-limit': "<spring:message code="label.additional-item-limit"/>",
        'done': "<spring:message code="label.done"/>",
        'cancel': "<spring:message code="label.cancel"/>"
    });

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
        <div id="freeitems"></div>
        <div class="deliverycheck"></div>
        <div id="checkoutcontainer"></div>
    </div>
</div>
