<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.search"/></title>
    <script type="text/javascript" src="${ctx}/resources/script/results.js"></script>
</head>

<body>

<script type="text/javascript">
var breadcrumbs = new HashTable();
breadcrumbs.setItem('<spring:message code="label.search"/>','/home.html');
breadcrumbs.setItem('${searchlocation}','');
</script>

<h2>Search Results</h2>
<c:forEach var="restaurant" items="${results}">
    <div class="result-wrapper">
        <a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}">${restaurant.name}</a>
        <div class="result-description">${restaurant.description}</div>
    </div>
</c:forEach>

</body>
</html>
