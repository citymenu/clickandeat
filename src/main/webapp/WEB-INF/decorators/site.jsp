<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>

<!doctype html>

<head>
    <meta charset="utf-8"/>
    <meta name="robots" content="all" />
    <meta http-equiv="expires" content="0">
    <%@ include file="/WEB-INF/jsp/taglibs_js.jsp" %>
	<link rel="shortcut icon" href="${resources}/images/favicon.png">

    <!-- Typekit -->
    <script type="text/javascript" src="//use.typekit.net/iwp4tpg.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>

    <!-- Have to load locally because of @font-face declaration -->
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/MyFontsWebfontsKit.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/main.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/header.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/content.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/footer.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/dialog.css"/>

    <!-- JQuery -->
    <script type="text/javascript" src="${resources}/jquery/script/jquery-1.8.0.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.mousewheel-3.0.6.pack.js"charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.scrollto.js" charset="utf-8"></script>

    <!-- Fancybox -->
    <link rel="stylesheet" href="${resources}/fancybox/source/jquery.fancybox.css?v=2.1.0" type="text/css" media="screen" charset="utf-8"/>
    <script type="text/javascript" src="${resources}/fancybox/source/jquery.fancybox.js?v=2.1.0" charset="utf-8"></script>

    <!-- Watermark -->
    <script type="text/javascript" src="${resources}/jquery/script/jquery.data.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.watermark.min.js" charset="utf-8"></script>

    <!-- Scripts -->
    <script type="text/javascript" src="${ctx}/app/script/messages.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/json2.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/tools.js" charset="utf-8"></script>

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
<script type="text/javascript" src="//assets.zendesk.com/external/zenbox/v2.5/zenbox.js" charset="utf-8"></script>
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

<!-- Google Tracking -->
<script type="text/javascript">
var _gaq = _gaq || [];
//_gaq.push(['_setAccount', 'UA-34980612-1']);
_gaq.push(['_setAccount', 'UA-36045848-1']);
_gaq.push(['_trackPageview']);

(function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript';
    ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(ga, s);
})();
</script>

</body>

</html>