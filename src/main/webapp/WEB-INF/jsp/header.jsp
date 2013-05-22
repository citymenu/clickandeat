<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:set var="path" value="${fn:substringAfter(pageContext.request.servletPath,'/WEB-INF/jsp/')}"/>

<c:choose>
    <c:when test="${path == 'en_UK/home.jsp' || path == 'es_ES/home.jsp'}">
        <c:set var="navstyle" value="workflow1"/>
    </c:when>
    <c:when test="${path == 'findRestaurant.jsp'}">
        <c:set var="navstyle" value="workflow2"/>
    </c:when>
    <c:when test="${path == 'restaurant.jsp'}">
        <c:set var="navstyle" value="workflow3"/>
    </c:when>
    <c:when test="${path == 'checkout.jsp' || path == 'payment.jsp' || path == 'en_UK/callNowSummary.jsp' || path == 'es_ES/callNowSummary.jsp'}">
        <c:set var="navstyle" value="workflow4"/>
    </c:when>
    <c:otherwise>
        <c:set var="navstyle" value="workflow5"/>
    </c:otherwise>
</c:choose>

<div id="header">
    <div id="topnav" class="${navstyle}">
        <div class="navigation-links">
            <table width="1020" class="unselectable">
                <tr valign="top">
                    <td width="260" align="center">
                        <a href="${ctx}/home.html"><message:message key="workflow.1-enter-your-location"/></a>
                    </td>
                    <td width="260" align="center">
                        <c:choose>
                            <c:when test="${path == 'findRestaurant.jsp' || search != null}">
                                <a href="${ctx}/app/<message:message key="url.find-takeaway"/>/session/loc"><message:message key="workflow.2-select-a-restaurant"/></a>
                            </c:when>
                            <c:otherwise>
                                <message:message key="workflow.2-select-a-restaurant"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td width="260" align="center">
                        <c:choose>
                            <c:when test="${path == 'restaurant.jsp' && orderrestaurantid != null && restaurantid != null && orderrestaurantid != restaurantid && search != null}">
                                <a href="${ctx}${orderrestauranturl}"><message:message key="workflow.3-build-your-order"/></a>
                            </c:when>
                            <c:when test="${path == 'restaurant.jsp'}">
                                <a href="${ctx}${restauranturl}"><message:message key="workflow.3-build-your-order"/></a>
                            </c:when>
                            <c:when test="${orderrestaurantid != null && search != null}">
                                <a href="${ctx}${orderrestauranturl}"><message:message key="workflow.3-build-your-order"/></a></li>
                            </c:when>
                            <c:when test="${restaurantid != null && search != null}">
                                <a href="${ctx}${restauranturl}"><message:message key="workflow.3-build-your-order"/></a>
                            </c:when>
                            <c:otherwise>
                                <message:message key="workflow.3-build-your-order"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td width="260" align="center">
                        <c:choose>
                            <c:when test="${path == 'checkout.jsp' || path == 'payment.jsp' || path == 'en_UK/callNowSummary.jsp' || path == 'es_ES/callNowSummary.jsp'}">
                                <a href="${ctx}/checkout.html"><message:message key="workflow.4-checkout"/></a>
                            </c:when>
                            <c:when test="${cancheckout != null && cancheckout == true}">
                                <a href="${ctx}/checkout.html"><message:message key="workflow.4-checkout"/></a>
                            </c:when>
                            <c:otherwise>
                                <message:message key="workflow.4-checkout"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>
        <div style="clear:both"></div>
    </div>
</div>
