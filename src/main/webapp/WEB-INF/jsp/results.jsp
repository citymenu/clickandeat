<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.search"/></title>
    <script type="text/javascript" src="${ctx}/resources/script/results.js"></script>
</head>

<body>

<div id="ordernav">
    <ul>
        <li><a href="${ctx}/search.html${search.queryString}">1) Find a Restaurant</a></li>
        <li><a href="${ctx}/buildOrder.html">2) Build Your Order</a></li>
        <li><a href="${ctx}/search.html">3) Checkout</a></li>
    </ul>
</div>

<div id="search" class="boxcontainer">
    <form method="GET" action="${ctx}/search.html">
        <h3><spring:message code="label.search"/></h3>
        <div>Location:</div>
        <div><input name="loc" type="text" class="search" value="${search.location}"/></div>
        <input type="submit" value="<spring:message code="label.search"/>"/>
        <div>Cuisines:</div>
        <c:forEach var="cuisine" items="${cuisines}">
            <c:set var="checked" value=""/>
            <c:forEach var="c" items="${search.cuisines}">
                <c:if test="${c eq cuisine}">
                    <c:set var="checked" value="checked='true'" />
                </c:if>
            </c:forEach>
            <div><input type="checkbox" name="c" value="${cuisine}" ${checked}/> ${cuisine}</div>
        </c:forEach>
    </form>
</div>

<div id="searchresults">

    <div id="searchheader" class="boxcontainer">
        <h3>Showing ${count} restaurants serving location ${search.location}</h3>
    </div>

    <c:forEach var="restaurant" items="${results}">
        <div class="boxcontainer">
            <a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}">${restaurant.name}</a>
            <div class="result-description">${restaurant.description}</div>
        </div>
    </c:forEach>
</div>


</body>
</html>
