<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.search"/></title>
</head>

<body>

<h2>Fully Open:</h2>
<c:forEach var="restaurant" items="${fullyOpen}">
    <div>${restaurant.name}</div>
</c:forEach>

<h2>Open For Collection Only:</h2>
<c:forEach var="restaurant" items="${openForCollection}">
    <div>${restaurant.name}</div>
</c:forEach>

<h2>Closed:</h2>
<c:forEach var="restaurant" items="${closed}">
    <div>${restaurant.name}</div>
</c:forEach>

</body>
</html>
