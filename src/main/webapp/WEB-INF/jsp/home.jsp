<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.home"/></title>
</head>

<body>
<h1>Current user: <%= request.getRemoteUser() %></h1>
<p><a href="${ctx}/secure/login.html"><spring:message code="label.login"/></a></p>
<p><a href="${ctx}/j_security_logout"><spring:message code="label.logout"/></a></p>
<p><a href="${ctx}/secure/register"><spring:message code="label.register"/></a></p>

<div>
    <p><spring:message code="label.change-language"/></p>
    <p><a href="?lang=en">en</a> | <a href="?lang=es">es</a></p>
</div>

</body>
</html>
