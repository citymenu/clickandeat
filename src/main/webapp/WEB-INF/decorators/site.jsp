<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="robots" content="all" />
    <meta http-equiv="expires" content="0">
	<link rel="shortcut icon" href="${resources}/images/favicon.png">

    <!-- Typekit -->
    <script type="text/javascript" src="//use.typekit.net/iwp4tpg.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>

    <!-- Have to load locally because of @font-face declaration -->
    <link rel="stylesheet" type="text/css" media="all" href="${ctx}/resources/css/MyFontsWebfontsKit.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/main.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/header.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/content.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/footer.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/dialog.css"/>

    <!-- JQuery -->
    <script type="text/javascript" src="${resources}/jquery/script/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.mousewheel-3.0.6.pack.js"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.scrollto.js"></script>

    <!-- Fancybox -->
    <link rel="stylesheet" href="${resources}/fancybox/source/jquery.fancybox.css?v=2.1.0" type="text/css" media="screen" />
    <script type="text/javascript" src="${resources}/fancybox/source/jquery.fancybox.js?v=2.1.0"></script>

    <!-- Watermark -->
    <script type="text/javascript" src="${resources}/jquery/script/jquery.data.js"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.watermark.min.js"></script>

    <!-- Scripts -->
    <script type="text/javascript" src="${ctx}/script/messages.html"></script>
    <script type="text/javascript" src="${resources}/script/json2.js"></script>
    <script type="text/javascript" src="${resources}/script/tools.js"></script>

    <!-- Apply fancybox -->
    <script type="text/javascript">
	    $(document).ready(function() {
    		$(".fancybox").fancybox();
    	});
    </script>

	<decorator:head/>

    <title><decorator:title/></title>

</head>

<body>

<!-- Zendesk -->
<script type="text/javascript" src="//assets.zendesk.com/external/zenbox/v2.5/zenbox.js"></script>
<style type="text/css" media="screen, projection">
  @import url(//assets.zendesk.com/external/zenbox/v2.5/zenbox.css);
  @import url(${resources}/css/zenbox_override.css);
</style>
<script type="text/javascript">
  if (typeof(Zenbox) !== "undefined") {
    Zenbox.init({
      dropboxID:   "20108247",
      url:         "https://llamarycomer.zendesk.com",
      hide_tab: true
    });
  }
</script>

<decorator:body/>
</body>

</html>