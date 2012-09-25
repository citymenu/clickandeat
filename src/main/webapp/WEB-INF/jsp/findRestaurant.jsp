<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>


<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/content.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/findrestaurant.css"/>
    <script type="text/javascript" src="${resources}/script/search.js"></script>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <title><message:message key="page-title.search-results" escape="false"/></title>

</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <!-- Cusine bar -->
                <td width="200">
                    <div class="cuisine-wrapper">
                        <h2><message:message key="search.choose-cuisine"/></h2>
                        <div class="cuisine-wrapper-description"><message:message key="search.choose-cuisine-instructions"/></div>
                        <div class="cuisine-entry-wrapper">
                            <c:forEach var="entry" items="${resultCount}" varStatus="status">
                                <c:if test="${status.count == 1}">
                                <div class="cuisine-wrapper-entry"><b><message:message key="search.all"/></b> (${entry.value})</div>
                                </c:if>
                                <c:if test="${status.count > 1}">
                                <div class="cuisine-wrapper-entry"><b><util:escape value="${entry.key}"/></b> (${entry.value})</div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </td>

                <!-- Search results -->
                <td width="560">
                    <div class="search-results-wrapper">
                        <div class="search-results-header-wrapper">
                            <h2><message:message key="search.your-location"/>: <util:escape value="${search.location.displayAddress}"/></h2>
                            <div class="search-results-detail">
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
                                <table width="500">
                                    <tr valign="top">
                                        <td width="65"><img src="${resources}/images/example.gif" width="65" height="50"/></td>
                                        <td width="285">
                                            <div class="search-result-detail">
                                                <h2><util:escape value="${restaurant.name}"/></h2>
                                                <c:if test="${restaurant.description != null}">
                                                <div class="restaurant-description"><util:escape value="${restaurant.description}" escapeNewLines="true"/></div>
                                                </c:if>
                                                <div class="restaurant-details">
                                                    <util:escape value="${restaurant.address.summary}"/><br>${restaurant.contactTelephone}
                                                </div>
                                            </div>
                                        </td>
                                        <td width="150" align="right">
                                            <a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}" class="search-result-button"><message:message key="search.order-now"/>
                                        </td>
                                    </tr>
                                </table>
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

