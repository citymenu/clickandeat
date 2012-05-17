<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.search"/></title>
</head>

<body>
<h1>Current user: <%= request.getRemoteUser() %></h1>
<p>Results: ${count}</p>
</body>
</html>
