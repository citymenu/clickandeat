<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div id="header">
    <div class="header-wrapper">
        <div class="header-banner">
            <div class="header-company unselectable">llamar y comer</div>
        </div>
        <div class="navigation-wrapper">
            <div class="navigation-links">
                <ul>
                    <li><a href="${ctx}/home.html">1. Enter your location</a></li>
                    <li class="arrow">&gt&gt</li>
                    <c:if test="${search != null}">
                    <li><a href="${ctx}/findRestaurant.html?loc=${search.location}">2. Select a restaurant</a></li>
                    </c:if>
                    <c:if test="${search == null}">
                    <li>2. Select a restaurant</li>
                    </c:if>
                    <li class="arrow">&gt&gt</li>
                    <c:if test="${restaurantid != null}">
                    <li><a href="${ctx}/restaurant.html?restaurantId=${restaurantid}">3. Build your order</a></li>
                    </c:if>
                    <c:if test="${restaurantid == null}">
                    <li>3. Build your order</li>
                    </c:if>
                    <li class="arrow">&gt&gt</li>
                    <c:if test="${cancheckout != null && cancheckout == true}">
                    <li><a href="${ctx}/secure/checkout.html">4. Checkout</a></li>
                    </c:if>
                    <c:if test="${cancheckout == null || cancheckout == false}">
                    <li>4. Checkout</li>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>
</div>
