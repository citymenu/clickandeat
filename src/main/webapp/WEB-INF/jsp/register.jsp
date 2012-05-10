<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.register"/></title>
</head>

<body>

<form:form commandName="user" id="register" method="post" action="${ctx}/secure/doRegister.html">
    <form:errors path="*" cssClass="errorBox" />
    <fieldset id="personalDetails">
        <legend><spring:message code="label.personal-details"/></legend>
        <div><spring:message code="label.firstname"/></div>
        <div><form:input path="person.firstName"/></div>
        <div><spring:message code="label.lastname"/></div>
        <div><form:input path="person.lastName"/></div>
        <div><spring:message code="label.telephone"/></div>
        <div><form:input path="person.telephone"/></div>
        <div><spring:message code="label.mobile"/></div>
        <div><form:input path="person.mobile"/></div>
    </fieldset>
    <fieldset id="addressDetails">
        <legend><spring:message code="label.address-details"/></legend>
        <div><spring:message code="label.streetaddress"/></div>
        <div><form:input path="address.streetAddress"/></div>
        <div><spring:message code="label.town"/></div>
        <div><form:input path="address.town"/></div>
        <div><spring:message code="label.region"/></div>
        <div><form:input path="address.region"/></div>
        <div><spring:message code="label.postcode"/></div>
        <div><form:input path="address.postCode"/></div>
    </fieldset>
    <fieldset id="loginDetails">
        <legend><spring:message code="label.login-details"/></legend>
        <div><spring:message code="label.email"/></div>
        <div><form:input path="username"/></div>
        <div><spring:message code="label.password"/></div>
        <div><form:password path="password"/></div>
        <div><spring:message code="label.confirm-password"/></div>
        <div><form:password path="confirmPassword"/></div>
    </fieldset>
    <div><input type="submit" value="<spring:message code="label.register"/>"/></div>
</form:form>

</body>