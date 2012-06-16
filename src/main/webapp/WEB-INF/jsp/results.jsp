<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.search"/></title>
</head>

<body>

<h2>Search Results</h2>
<c:forEach var="restaurant" items="${results}">
    <div class="result-wrapper">
        <a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}">${restaurant.name}</a>
        <div class="result-description">${restaurant.description}</div>
    </div>
</c:forEach>

</body>
</html>
