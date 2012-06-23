<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<script type="text/javascript">
    var order = jQuery.parseJSON('${orderjson}');
</script>

<div class="order">
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
            </tbody>
        </table>
    </div>
    <div class="ordertotal">
        <div class="ordertotaltext"><spring:message code="label.totalprice"/></div>
        <div class="totalcost"><span class='totalitemcost'></span></div>
    </div>
</div>