<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="robots" content="all" />
	<link rel="shortcut icon" href="${resources}/images/favico.png">
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/ext/resources/css/ext-all.css"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/overrides.css"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/messagebox.css"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/master.css"/>
	<!-- ExtJS -->
	<script type="text/javascript" src="${resources}/ext/ext-all-debug.js"></script>
	<script type="text/javascript" src="${resources}/ext/bootstrap.js"></script>
	<script type="text/javascript" src="${resources}/script/admin/extoverrides.js"></script>
	<script type="text/javascript" src="${resources}/script/admin/util.js"></script>
    <decorator:head/>
    <title><decorator:title/></title>
</head>
<body>
<div id="container">
    <decorator:body/>
</div>
</body>
</html>