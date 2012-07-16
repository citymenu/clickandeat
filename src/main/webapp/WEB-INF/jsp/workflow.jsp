<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<c:set var="workflowUrl" value="${pageContext.request.requestURI}"/>

<div id="workflow">
    <table class="tabstable" cellpadding="0" cellspacing="0" border="0" width="100%">
        <thead>
            <tr>
                <th width="25%"><div class="workflowtab workflowtableft <c:if test="${workflowUrl=='/WEB-INF/jsp/findRestaurant.jsp'}">workflowtabactive</c:if>">1. <spring:message code="label.find-restaurant"/></div></th>
                <th width="25%"><div class="workflowtab <c:if test="${workflowUrl=='/WEB-INF/jsp/restaurant.jsp'}">workflowtabactive</c:if>">2. <spring:message code="label.build-order"/></div></th>
                <th width="25%"><div class="workflowtab <c:if test="${workflowUrl=='/WEB-INF/jsp/checkout.jsp'}">workflowtabactive</c:if>">3. <spring:message code="label.checkout"/></div></th>
                <th width="25%"><div class="workflowtab workflowtabright <c:if test="${workflowUrl=='/WEB-INF/jsp/findRestaurant.jsp'}">workflowtabactive</c:if>">4. <spring:message code="label.payment"/></div></th>
            </tr>
        </thead>
    </table>
</div>