<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title>Login</title>
</head>

<body>

<form method="post" action="${ctx}/secure/j_security_check">
    <div><span>User:</span><input type="text" name="j_username"/></div>
    <div><span>Password:</span><input type="password" name="j_password"/></div>
    <input type="submit" value="Login"/>
</form>

</body>