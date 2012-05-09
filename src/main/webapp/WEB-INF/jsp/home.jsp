<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<body>
<h1>Current user: <%= request.getRemoteUser() %></h1>
<p><a href="${ctx}/secure/login.html">Login</a></p>
<p><a href="${ctx}/j_security_logout">Logout</a></p>
</body>
</html>
