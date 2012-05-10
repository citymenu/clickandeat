<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title>Login</title>
</head>

<body>

<form id="login" method="post" action="${ctx}/secure/j_security_check">
    <fieldset id="loginFieldset">
        <div>User:</div>
        <div><input type="text" name="j_username"/></div>
        <div>Password:</div>
        <div><input type="password" name="j_password"/></div>
        <div><input type="submit" value="Login"/></div>
    </fieldset>
</form>

</body>