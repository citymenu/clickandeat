<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.register"/></title>
</head>

<body>

<form id="register" method="post" action="${ctx}/secure/register.html">
    <fieldset id="personalDetails">
        <legend><spring:message code="label.personal-details"/></legend>
        <div><spring:message code="label.firstname"/></div>
        <div><input type="text" name="firstName"/></div>
        <div><spring:message code="label.lastname"/></div>
        <div><input type="text" name="lastName"/></div>
        <div><spring:message code="label.telephone"/></div>
        <div><input type="text" name="telephone"/></div>
        <div><spring:message code="label.mobile"/></div>
        <div><input type="text" name="mobile"/></div>
    </fieldset>
    <fieldset id="addressDetails">
        <legend><spring:message code="label.address-details"/></legend>
        <div><spring:message code="label.streetaddress"/></div>
        <div><input type="text" name="streetAddress"/></div>
        <div><spring:message code="label.town"/></div>
        <div><input type="text" name="town"/></div>
        <div><spring:message code="label.region"/></div>
        <div><input type="text" name="region"/></div>
        <div><spring:message code="label.postcode"/></div>
        <div><input type="text" name="postCode"/></div>
    </fieldset>
    <fieldset id="loginDetails">
        <legend><spring:message code="label.login-details"/></legend>
        <div><spring:message code="label.email"/></div>
        <div><input type="text" name="username"/></div>
        <div><spring:message code="label.password"/></div>
        <div><input type="password" name="password"/></div>
        <div><spring:message code="label.confirm-password"/></div>
        <div><input type="password" name="confirmpassword"/></div>
    </fieldset>
    <div><input type="submit" value="<spring:message code="label.register"/>"/></div>
</form>

</body>