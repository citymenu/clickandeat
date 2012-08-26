<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="robots" content="all" />
    <meta http-equiv="expires" content="0">
	<link rel="shortcut icon" href="${resources}/images/favico.png">
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/jquery/css/sunny/jquery-ui-1.8.20.custom.css"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/master.css"/>
	<script type="text/javascript" src="${resources}/script/tools.js"></script>
	<script type="text/javascript" src="${resources}/script/breadcrumbs.js"></script>
	<script type="text/javascript" src="${resources}/script/json2.js"></script>
	<script type="text/javascript" src="${resources}/jquery/script/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery-ui-1.8.20.custom.min.js"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery-validate.js"></script>
    <script type="text/javascript" src="${resources}/jquery/i18n/jquery.ui.datepicker-${locale}.js"></script>
	<decorator:head/>
    <title><decorator:title/></title>
</head>
<body>
<div id="header">
    <div id="titlenav">
        <ul>
            <li><a href="${ctx}/home.html">Home</a></li>
            <li><a href="${ctx}/admin/restaurants.html">Admin</a></li>
        </ul>
    </div>
</div>
<div id="container">
    <decorator:body/>
</div>
</body>
</html>