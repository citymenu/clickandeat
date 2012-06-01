<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.home"/></title>
    <script type="text/javascript" src="${ctx}/resources/script/home.js"></script>
</head>

<body>
<h1>Current user: <%= request.getRemoteUser() %></h1>
<h1>Current locale: <%= response.getLocale() %></h1>
<h1>Session id: <%= request.getSession().getId() %></h1>

<c:if test="${pageContext.request.remoteUser == null}">
    <p><a href="${ctx}/secure/login.html"><spring:message code="label.login"/></a></p>
    <p><a href="${ctx}/secure/register.html"><spring:message code="label.register"/></a></p>
</c:if>

<c:if test="${pageContext.request.remoteUser != null}">
    <p><a href="${ctx}/j_security_logout"><spring:message code="label.logout"/></a></p>
</c:if>

<div>
    <p><spring:message code="label.change-language"/></p>
    <p><a href="?lang=en">en</a> | <a href="?lang=es">es</a></p>
</div>

<div>
    <p><spring message code="label.search"/></p>
    <form method="get" action="${ctx}/search.html">
        <input type="text" name="loc" id="loc"/> <input type="submit" value="<spring:message code="label.search"/>"/>
    </form>
</div>

</body>
</html>
