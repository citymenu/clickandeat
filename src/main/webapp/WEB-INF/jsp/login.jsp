<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.login"/></title>
    <script type="text/javascript" src="${resources}/script/login.js"></script>
</head>

<body>

<form id="login" method="post" action="${ctx}/secure/j_security_check">
    <fieldset id="loginFieldset">
        <div><spring:message code="label.email"/></div>
        <div><input type="text" name="j_username"/></div>
        <div><spring:message code="label.password"/></div>
        <div><input type="password" name="j_password"/></div>
        <div><input type="submit" value="<spring:message code="label.login"/>"/></div>
    </fieldset>
</form>

</body>