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
        'order-for-delivery': "<spring:message code="label.order-for-delivery"/>",
        'order-for-collection': "<spring:message code="label.order-for-collection"/>",
        'delivery-charge': "<spring:message code="label.delivery-charge"/>",
        'collection-discount': "<spring:message code="label.collection-discount"/>",
        'delivery-warning': "<spring:message code="label.delivery-warning"/>",
        'restaurant-delivery-closed-warning': "<spring:message code="label.restaurant-delivery-closed-warning"/>",
        'restaurant-collection-closed-warning': "<spring:message code="label.restaurant-collection-closed-warning"/>",
        'restaurant-is-not-open-warning': "<spring:message code="label.restaurant-is-not-open-warning"/>",
        'checkout': "<spring:message code="label.checkout"/>",
        'restaurant-warning': "<spring:message code="label.restaurant-warning"/>",
        'are-you-sure': "<spring:message code="label.are-you-sure"/>",
        'add-item-anyway': "<spring:message code="label.add-item-anyway"/>",
        'dont-add-item': "<spring:message code="label.dont-add-item"/>",
        'special-offer-not-available-delivery': "<spring:message code="label.special-offer-not-available-delivery"/>",
        'special-offer-not-available-collection': "<spring:message code="label.special-offer-not-available-collection"/>",
        'remove-from-order': "<spring:message code="label.remove-from-order"/>",
        'update-order': "<spring:message code="label.update-order"/>",
        'no-thanks': "<spring:message code="label.no-thanks"/>",
        'free': "<spring:message code="label.free"/>",
        'choose-additional': "<spring:message code="label.choose-additional"/>",
        'special-offer-choices': "<spring:message code="label.special-offer-choices"/>",
        'additional-item-limit': "<spring:message code="label.additional-item-limit"/>",
        'done': "<spring:message code="label.done"/>",
        'cancel': "<spring:message code="label.cancel"/>",
        'today': "<spring:message code="label.today"/>",
        'asap': "<spring:message code="label.asap"/>",
        'day-of-week-1': "<spring:message code="label.day-of-week-1"/>",
        'day-of-week-2': "<spring:message code="label.day-of-week-2"/>",
        'day-of-week-3': "<spring:message code="label.day-of-week-3"/>",
        'day-of-week-4': "<spring:message code="label.day-of-week-4"/>",
        'day-of-week-5': "<spring:message code="label.day-of-week-5"/>",
        'day-of-week-6': "<spring:message code="label.day-of-week-6"/>",
        'day-of-week-7': "<spring:message code="label.day-of-week-7"/>"
    });

</script>

<div class="order-wrapper">
    <div class='orderheader'><spring:message code="label.your-order"/>:</div>
    <div class="order-delivery-wrapper"></div>
    <div id="deliverycheck"></div>
    <div class="order-items-wrapper">
        <div class="order-item-wrapper-header">
            <table width="236">
                <tr>
                    <td width="146" align="left"><h3 class="order-table"><spring:message code="label.item"/></h3></td>
                    <td width="60" align="center"><h3 class="order-table"><spring:message code="label.price"/></h3></td>
                    <td width="30"></td>
                </tr>
                <tbody>
                </tbody>
            </table>
        </div>
        <div id="order-item-contents"></div>
    </div>
    <div id="freeitems"></div>
    <div id="ordertotal"><spring:message code="label.order-total"/>:</div>
    <div id="checkoutcontainer"></div>
</div>
