<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>


<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/content.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/findrestaurant.css"/>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <title><message:message key="page-title.search-results" escape="false"/></title>

</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <!-- Search results -->
                <td width="760">
                    <div class="search-results-wrapper">
                        <div class="search-results-header-wrapper">
                            <h2><message:message key="search.your-location"/>: <util:escape value="${search.location.displayAddress}"/></h2>
                            <c:if test="${count > 0 && search.location.radiusWarning == true}">
                            <div class="search-results-warning">
                                <message:message key="search.location-radius-warning"/>
                            </div>
                            </c:if>
                            <div class="search-results-detail">
                                <c:if test="${count > 0 && search.location.radiusWarning == true}">
                                </c:if>
                                <c:choose>
                                    <c:when test="${count == 0}">
                                        <message:message key="search.no-results-found"/>
                                    </c:when>
                                    <c:when test="${search.cuisine != null && search.cuisine != ''}">
                                        <message:message key="search.showing"/> ${count} <util:escape value="${search.cuisine}"/> <message:message key="search.restaurants-serving-your-location"/>
                                    </div>
                                    </c:when>
                                    <c:otherwise>
                                        <message:message key="search.showing"/> ${count} <message:message key="search.restaurants-serving-your-location"/>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <c:if test="${count > 0}">
                        <div class="search-results-entry-wrapper">
                            <c:forEach var="restaurant" items="${results}">
                            <div class="search-result-wrapper">
                                <div class="search-result">
                                    <table width="730">
                                        <tr valign="top">
                                            <td width="65"><img src="${resources}/images/restaurant/${restaurant.imageName}" width="65" height="50" alt="<util:escape value="${restaurant.name}"/>"/></td>
                                            <td width="315">
                                                <div class="search-result-center">
                                                    <h2><util:escape value="${restaurant.name}"/></h2>
                                                    <div class="address-details"><util:escape value="${restaurant.address.summary}"/></div>
                                                    <div class="cuisine-summary"><util:escape value="${restaurant.cuisineSummary}"/></div>
                                                </div>
                                            </td>
                                            <td width="250">
                                                <c:if test="${restaurant.hasDiscounts == true}">
                                                <div class="restaurant-discount-details">
                                                    <div class="scissors"></div>
                                                    <c:forEach var="discount" items="${restaurant.discounts}">
                                                        <div class="discount-details"><util:escape value="${discount.title}"/></div>
                                                    </c:forEach>
                                                </div>
                                                </c:if>
                                            </td>
                                            <td width="150" align="right">
                                                <div class="menu-link">
                                                    <a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}" class="search-result-button"><message:message key="search.order-now"/></a>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                    <table width="730">
                                        <tr valign="top">
                                            <td width="730">
                                                <div class="restaurant-opening-details">
                                                    <div class="opening-details"><message:message key="search.open-today"/>: ${restaurant.todaysOpeningTimes}</div>
                                                    <div class="delivery-details"><util:escape value="${restaurant.deliveryOptions.deliveryOptionsSummary}" escapeNewLines="true" escapeComments="true"/></div>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                            </c:forEach>
                        </div>
                        </c:if>

                    </div>
                </td>

                <!-- Order panel -->
                <td width="260">
                    <div class="search-results-right">
                        <%@ include file="/WEB-INF/jsp/order.jsp" %>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>

</body>
</html>

