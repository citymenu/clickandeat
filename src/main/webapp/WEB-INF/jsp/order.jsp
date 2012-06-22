<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<script type="text/javascript">
    var order = jQuery.parseJSON('${orderjson}');
</script>

<div class="order">
    <div class="orderitems">
        <table>
            <thead>
                <tr>
                    <th><spring:message code="label.item"/></th>
                    <th><spring:message code="label.quantity"/></th>
                    <th><spring:message code="label.price"/></th>
                    <th></th>
                </tr>
            </thead>
            <tbody class="orderbody">
            </tbody>
        </table>
    </div>
    <div class="ordertotal">
        <div class="ordertotaltext"><spring:message code="label.totalprice"/></div>
        <div class="totalcost"><span class='totalitemcost'></span></div>
    </div>
</div>