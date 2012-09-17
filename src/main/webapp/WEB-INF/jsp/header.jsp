<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:set var="path" value="${fn:substringAfter(pageContext.request.servletPath,'/WEB-INF/jsp/')}"/>

<div id="header">
    <div class="header-wrapper">
        <div class="header-banner">
            <div class="header-company unselectable">llamar y comer</div>
        </div>
        <div class="navigation-wrapper">
            <div class="navigation-links">
                <ul>
                    <c:choose>
                        <c:when test="${path == 'home.jsp'}">
                            <li class="active unselectable">1. Enter your location</li>
                        </c:when>
                        <c:otherwise>
                            <li class="unselectable"><a href="${ctx}/home.html">1. Enter your location</a></li>
                        </c:otherwise>
                    </c:choose>

                    <li class="arrow">&gt&gt</li>

                    <c:choose>
                        <c:when test="${path == 'findRestaurant.jsp'}">
                            <li class="active unselectable">2. Select a restaurant</li>
                        </c:when>
                        <c:when test="${search != null}">
                            <li class="unselectable"><a href="${ctx}/findRestaurant.html?loc=${search.location}">2. Select a restaurant</a></li>
                        </c:when>
                        <c:otherwise>
                            <li>2. Select a restaurant</li>
                        </c:otherwise>
                    </c:choose>

                    <li class="arrow">&gt&gt</li>

                    <c:choose>
                        <c:when test="${path == 'restaurant.jsp'}">
                            <li class="active unselectable">3. Build your order</li>
                        </c:when>
                        <c:when test="${restaurantid != null}">
                            <li class="unselectable"><a href="${ctx}/restaurant.html?restaurantId=${restaurantid}">3. Build your order</a></li>
                        </c:when>
                        <c:otherwise>
                            <li>3. Build your order</li>
                        </c:otherwise>
                    </c:choose>

                    <li class="arrow">&gt&gt</li>

                    <c:choose>
                        <c:when test="${path == 'checkout.jsp' || path == 'payment.jsp'}">
                            <li class="active unselectable">4. Checkout</li>
                        </c:when>
                        <c:when test="${cancheckout != null && cancheckout == true}">
                            <li class="unselectable"><a href="${ctx}/secure/checkout.html">4. Checkout</a></li>
                        </c:when>
                        <c:otherwise>
                            <li>4. Checkout</li>
                        </c:otherwise>
                    </c:choose>

                </ul>
            </div>
        </div>
    </div>
</div>
