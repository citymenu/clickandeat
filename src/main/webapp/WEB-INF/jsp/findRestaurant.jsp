<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>


<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/content.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>
    <script type="text/javascript" src="${resources}/script/search.js"></script>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/findrestaurant.css"/>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <script type="text/javascript" src="${resources}/script/findrestaurant.js"></script>
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
                            <h2><message:message key="search.search-results"/></h2>
                            <c:choose>
                                <c:when test="${search == null || search.location == null}">
                                    <div class="location-warning"><message:message key="search.location-not-set-warning"/></div>
                                </c:when>
                                <c:when test="${search.location.radiusWarning == true}">
                                    <div class="location-warning"><message:message key="search.location-radius-warning"/></div>
                                </c:when>
                            </c:choose>
                            <div class="search-location-wrapper">
                                <div class="search-location-edit">
                                    <c:choose>
                                        <c:when test="${search == null || search.location == null}">
                                            <div class="search-location"><message:message key="search.location-not-set"/> <a class="location-button" id="changeLocation"><message:message key="button.change"/></a></div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="search-location"><message:message key="search.your-location"/> "${search.location.displayAddress}" <a class="location-button" id="changeLocation"><message:message key="button.change"/></a></div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <c:choose>
                                    <c:when test="${count > 0}">
                                        <div class="search-location-results"><message:message key="search.restaurants-serving-your-location" format="${count}"/></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="search-location-results"><message:message key="search.no-restaurants-found" format="${count}"/></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <c:if test="${count > 0}">
                            <div class="search-filters">
                                <table width="690">
                                    <tr valign="middle">
                                        <td width="490">
                                            <div class="search-cuisine-filter">
                                                <message:message key="search.filter-by-cuisine"/>:
                                                <select class="search-select" id="cuisine-select">
                                                    <option value=""><message:message key="search.all"/> (${count})</option>
                                                    <c:forEach var="entry" items="${resultCount}" varStatus="status">
                                                    <option value="${entry.key}"><util:escape value="${entry.key}"/> (${entry.value})</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </td>
                                        <td width="200" align="right">
                                            <div class="search-cuisine-filter">
                                                <input type="checkbox" class="search-checkbox" id="ignore-closed"/> <message:message key="search.ignore-closed"/>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            </c:if>
                        </div>

                        <c:if test="${count > 0}">
                        <div class="search-results-entry-wrapper">
                            <c:forEach var="restaurant" items="${results}">
                            <div class="search-result-wrapper" open="${restaurant.openForDelivery}" cuisines="${restaurant.cuisineSummary}">
                                <div class="search-result">
                                    <table width="710">
                                        <tr valign="top">
                                            <td width="65"><img src="${resources}/images/restaurant/${restaurant.imageName}" width="65" height="50" alt="<util:escape value="${restaurant.name}"/>"/></td>
                                            <c:choose>
                                                <c:when test="${restaurant.hasDiscounts == true}">
                                                    <td width="295">
                                                        <div class="search-result-center">
                                                            <h2><util:escape value="${restaurant.name}"/></h2>
                                                            <div class="address-details"><util:escape value="${restaurant.address.summary}"/></div>
                                                            <div class="cuisine-summary"><util:escape value="${restaurant.cuisineSummary}"/></div>
                                                        </div>
                                                    </td>
                                                    <td width="250">
                                                        <div class="restaurant-discount-details">
                                                            <div class="scissors"></div>
                                                            <c:forEach var="discount" items="${restaurant.discounts}">
                                                                <div class="discount-details"><util:escape value="${discount.title}"/></div>
                                                            </c:forEach>
                                                        </div>
                                                    </td>
                                                </c:when>
                                                <c:otherwise>
                                                    <td width="545">
                                                        <div class="search-result-center">
                                                            <h2><util:escape value="${restaurant.name}"/></h2>
                                                            <div class="address-details"><util:escape value="${restaurant.address.summary}"/></div>
                                                            <div class="cuisine-summary"><util:escape value="${restaurant.cuisineSummary}"/></div>
                                                        </div>
                                                    </td>
                                                </c:otherwise>
                                            </c:choose>
                                            <td width="150" align="right">
                                                <div class="menu-link">
                                                    <a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}" class="search-result-button"><message:message key="search.order-now"/></a>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                    <table width="710">
                                        <tr valign="top">
                                            <td width="710">
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

