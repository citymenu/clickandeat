<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>

<!doctype html>

<html>
<head>

    <meta charset="utf-8"/>
    <meta name="robots" content="all" />
    <meta http-equiv="expires" content="0">
    <%@ include file="/WEB-INF/jsp/taglibs_js.jsp" %>

	<link rel="shortcut icon" href="${resources}/images/favico.png">
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/ext/resources/css/ext-all.css" charset="utf-8"/>

    <!-- Typekit -->
    <script type="text/javascript" src="//use.typekit.net/iwp4tpg.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>

	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/overrides.css" charset="utf-8"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/messagebox.css" charset="utf-8"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/MyFontsWebfontsKit.css" charset="utf-8"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/main.css" charset="utf-8"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/header-admin.css" charset="utf-8"/>
	<link rel="stylesheet" type="text/css" media="all" href="${resources}/css/admin.css" charset="utf-8"/>

    <!-- JQuery -->
    <script type="text/javascript" src="${resources}/jquery/script/jquery-1.8.0.min.js" charset="utf-8"></script>
	<!-- ExtJS -->
	<script type="text/javascript" src="${resources}/ext/ext-all-debug.js" charset="utf-8"></script>
	<script type="text/javascript" src="${resources}/ext/bootstrap.js" charset="utf-8"></script>
	<script type="text/javascript" src="${resources}/script/admin/extoverrides.js" charset="utf-8"></script>
	<script type="text/javascript" src="${resources}/script/admin/util.js" charset="utf-8"></script>
    <decorator:head/>
    <title><decorator:title/></title>
</head>
<body>
<div id="container">
    <decorator:body/>
</div>
</body>
</html>