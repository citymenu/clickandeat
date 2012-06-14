<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.search"/></title>
</head>

<body>

<h2>Fully Open:</h2>
<c:forEach var="restaurant" items="${fullyOpen}">
    <div><a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}">${restaurant.name}</a></div>
</c:forEach>

<h2>Open For Collection Only:</h2>
<c:forEach var="restaurant" items="${openForCollection}">
    <div><a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}">${restaurant.name}</a></div>
</c:forEach>

<h2>Closed:</h2>
<c:forEach var="restaurant" items="${closed}">
    <div><a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}">${restaurant.name}</a></div>
</c:forEach>

</body>
</html>
