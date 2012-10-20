<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:set var="path" value="${fn:substringAfter(pageContext.request.servletPath,'/WEB-INF/jsp/')}"/>

<div id="header">
    <div class="header-wrapper">
        <div class="header-banner">
            <div class="header-company unselectable">llamar<span class="header-company-small">y</span>comer</div>
            <div class="header-tagline unselectable"><message:message key="home.tagline" escape="false"/></div>
        </div>
        <div class="navigation-wrapper">
            <div class="navigation-links">
                <ul>
                    <c:choose>
                        <c:when test="${path == 'en_UK/home.jsp' || path == 'es_ES/home.jsp'}">
                            <li class="active unselectable"><message:message key="workflow.1-enter-your-location"/></li>
                        </c:when>
                        <c:otherwise>
                            <li class="unselectable"><a href="${ctx}/home.html"><message:message key="workflow.1-enter-your-location"/></a></li>
                        </c:otherwise>
                    </c:choose>

                    <li class="arrow">&gt&gt</li>

                    <c:choose>
                        <c:when test="${path == 'findRestaurant.jsp'}">
                            <li class="active unselectable"><message:message key="workflow.2-select-a-restaurant"/></li>
                        </c:when>
                        <c:when test="${search != null}">
                            <li class="unselectable"><a href="${ctx}/findRestaurant.html"><message:message key="workflow.2-select-a-restaurant"/></a></li>
                        </c:when>
                        <c:otherwise>
                            <li class="unselectable"><message:message key="workflow.2-select-a-restaurant"/></li>
                        </c:otherwise>
                    </c:choose>

                    <li class="arrow">&gt&gt</li>

                    <c:choose>
                        <c:when test="${path == 'restaurant.jsp' && orderrestaurantid != null && restaurantid != null && orderrestaurantid != restaurantid && search != null}">
                            <li class="active unselectable"><a href="${ctx}/restaurant.html?restaurantId=${orderrestaurantid}"><message:message key="workflow.3-build-your-order"/></a></li>
                        </c:when>
                        <c:when test="${path == 'restaurant.jsp'}">
                            <li class="active unselectable"><message:message key="workflow.3-build-your-order"/></li>
                        </c:when>
                        <c:when test="${orderrestaurantid != null && search != null}">
                            <li class="unselectable"><a href="${ctx}/restaurant.html?restaurantId=${orderrestaurantid}"><message:message key="workflow.3-build-your-order"/></a></li>
                        </c:when>
                        <c:when test="${restaurantid != null && search != null}">
                            <li class="unselectable"><a href="${ctx}/restaurant.html?restaurantId=${restaurantid}"><message:message key="workflow.3-build-your-order"/></a></li>
                        </c:when>
                        <c:otherwise>
                            <li class="unselectable"><message:message key="workflow.3-build-your-order"/></li>
                        </c:otherwise>
                    </c:choose>

                    <li class="arrow">&gt&gt</li>

                    <c:choose>
                        <c:when test="${path == 'checkout.jsp' || path == 'payment.jsp' || path == 'en_UK/callNowSummary.jsp' || path == 'es_ES/callNowSummary.jsp'}">
                            <li class="active unselectable"><message:message key="workflow.4-checkout"/></li>
                        </c:when>
                        <c:when test="${cancheckout != null && cancheckout == true}">
                            <li class="unselectable"><a href="${ctx}/checkout.html"><message:message key="workflow.4-checkout"/></a></li>
                        </c:when>
                        <c:otherwise>
                            <li class="unselectable"><message:message key="workflow.4-checkout"/></li>
                        </c:otherwise>
                    </c:choose>

                </ul>
            </div>
        </div>
    </div>
</div>
