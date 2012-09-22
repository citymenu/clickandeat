<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>


<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>
    <script type="text/javascript" src="${resources}/script/search.js"></script>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <title><message:message key="page-title.search-results" escape="false"/></title>

</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <div class="content-left">
            <table width="700">
                <tr valign="top">
                    <td width="250">
                        <h3><message:message key="search.search"/></h3>
                        <div>Location:</div>
                        <div><input id="loc" type="text" class="search" value="<util:escape value="${search.location.address}"/>"/></div>
                        <input type="button" id="searchbutton" value="<message:message key="button.search"/>"/>
                        <div><message:message key="config.cuisines"/></div>
                        <c:forEach var="cuisine" items="${cuisines}">
                            <c:set var="checked" value=""/>
                            <c:forEach var="c" items="${search.cuisines}">
                                <c:if test="${c eq cuisine}">
                                    <c:set var="checked" value="checked='true'" />
                                </c:if>
                            </c:forEach>
                            <div><input type="checkbox" name="c" value="${cuisine}" ${checked}/> ${cuisine}</div>
                        </c:forEach>
                    </td>
                    <td width="450">
                        <div id="searchresultsright">
                            <div id="results">
                                <div id="searchheader" class="boxcontainer">
                                    <div><message:message key="search.your-location"/>: <util:escape value="${search.location.displayAddress}"/></div>
                                    <div>${count} <message:message key="search.restaurants-found"/></div>
                                </div>
                                <c:forEach var="restaurant" items="${results}">
                                    <div class="boxcontainer searchresult">
                                        <a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}"><util:escape value="${restaurant.name}"/></a>
                                        <div class="result-description"><util:escape value="${restaurant.description}" escapeNewLines="true"/></div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <div class="content-right">
            <%@ include file="/WEB-INF/jsp/order.jsp" %>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>

</body>
</html>
