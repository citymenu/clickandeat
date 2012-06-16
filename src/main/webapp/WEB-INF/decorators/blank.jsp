<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="robots" content="all" />
	<link rel="shortcut icon" href="${ctx}/resources/images/favico.png">
	<link rel="stylesheet" type="text/css" media="all" href="${ctx}/resources/jquery/css/sunny/jquery-ui-1.8.20.custom.css"/>
	<link rel="stylesheet" type="text/css" media="all" href="${ctx}/resources/css/master.css"/>
	<script type="text/javascript" src="${ctx}/resources/jquery/script/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${ctx}/resources/jquery/script/jquery-ui-1.8.20.custom.min.js"></script>
    <script type="text/javascript" src="${ctx}/resources/jquery/i18n/jquery.ui.datepicker-${locale}.js"></script>
	<decorator:head/>
    <title><decorator:title/></title>
</head>
<body>
<div id="header">
    <div id="titlenav">
        <ul>
            <li><a href="${ctx}/home.html">Home</a></li>
            <li><a href="${ctx}/admin/restaurants.html">Restaurants</a></li>
            <c:if test="${user == null}">
                <li><a href="${ctx}/secure/login.html"><spring:message code="label.login"/></a></li>
                <li><a href="${ctx}/secure/register.html"><spring:message code="label.register"/></a></li>
            </c:if>
            <c:if test="${user != null}">
                <li><a href="${ctx}/j_security_logout"><spring:message code="label.logout"/></a></li>
            </c:if>
        </ul>
    </div>
</div>
<div id="container">
    <decorator:body/>
</div>
</body>
</html>