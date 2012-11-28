<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:set var="path" value="${fn:substringAfter(pageContext.request.servletPath,'/WEB-INF/jsp/')}"/>

<div id="header">
    <div class="header-wrapper">
        <div class="header-banner">
            <div class="header-company unselectable">llamar<span class="header-company-small">y</span>comer</div>
        </div>
        <div class="navigation-wrapper">
            <div class="navigation-links">
                <ul>
                    <c:choose>
                        <c:when test="${path == 'admin/restaurants.jsp' || path == 'admin/editRestaurant.jsp'}">
                            <li class="active unselectable">Restaurants</li>
                        </c:when>
                        <c:otherwise>
                            <li class="unselectable"><a href="${ctx}/admin/restaurants.html">Restaurants</a></li>
                        </c:otherwise>
                    </c:choose>

                    <li class="arrow">|</li>

                    <c:choose>
                        <c:when test="${path == 'admin/orders.jsp'}">
                            <li class="active unselectable">Orders</li>
                        </c:when>
                        <c:otherwise>
                            <li class="unselectable"><a href="${ctx}/admin/orders.html">Orders</a></li>
                        </c:otherwise>
                    </c:choose>

                    <li class="arrow">|</li>

                    <c:choose>
                        <c:when test="${path == 'admin/registrations.jsp'}">
                            <li class="active unselectable">Registrations</li>
                        </c:when>
                        <c:otherwise>
                            <li class="unselectable"><a href="${ctx}/admin/registrations.html">Registrations</a></li>
                        </c:otherwise>
                    </c:choose>

                </ul>
            </div>
        </div>
    </div>
</div>
