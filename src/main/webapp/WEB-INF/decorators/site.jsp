<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/locale.tld" prefix="locale" %>
<%@ taglib uri="/WEB-INF/tld/message.tld" prefix="message" %>
<%@ taglib uri="/WEB-INF/tld/locale.tld" prefix="locale" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="locale" value="${pageContext.response.locale}"/>
<c:set var="user" value="${pageContext.request.remoteUser}"/>
<locale:locale/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"><html lang="<locale:language/>">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="robots" content="all">
    <meta http-equiv="expires" content="0">
    <meta name="viewport" content="width=1050">
    <%@ include file="/WEB-INF/jsp/taglibs_js.jsp" %>
	<link rel="shortcut icon" href="${resources}/images/favicon.png">

    <!-- Typekit -->
    <script type="text/javascript" src="//use.typekit.net/iwp4tpg.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>

    <!-- Stylesheets -->
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/MyFontsWebfontsKit.css">
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/main.css">
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/header.css">
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/content.css">
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/footer.css">
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/dialog.css">

    <!-- JQuery -->
    <script type="text/javascript" src="${resources}/jquery/script/jquery-1.8.2.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.mousewheel-3.0.6.pack.js"charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.scrollto.js" charset="utf-8"></script>

    <!-- Fancybox -->
    <link rel="stylesheet" href="${resources}/fancybox/source/jquery.fancybox.css" type="text/css" media="screen" charset="utf-8">
    <script type="text/javascript" src="${resources}/fancybox/source/jquery.fancybox.js" charset="utf-8"></script>

    <!-- Watermark -->
    <script type="text/javascript" src="${resources}/jquery/script/jquery.data.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.watermark.min.js" charset="utf-8"></script>
    <script type="text/javascript">var watermark="<message:message key="search.watermark"/>";</script>

    <!-- Scripts -->
    <script type="text/javascript" src="${ctx}/app/script/messages.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/json2.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/tools.js" charset="utf-8"></script>

    <!-- CSS Overrides -->
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/overrides.css">

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
          dropboxID:   "20117406",
          url:         "https://llamarycomer.zendesk.com",
          hide_tab: true
        });
      }
    </script>

    <div id="wrapper">
        <div id="wrap">
            <div id="wrap-in">
                <div id="header-wrap">
                    <div id="page">
                        <div id="page-nav">
                            <a href="${ctx}/help.html"><message:message key="label.help"/></a> |
                            <a onclick="Zenbox.show()"><message:message key="label.feedback"/></a>
                        </div>
                    </div>
                    <div id="page">
                        <div id="header-container">
                            <div id="header-inner">
                                <div class="header-company-text unselectable"><a href="${ctx}/home.html" class="header-blank">llamar<span class="header-company-small">y</span>comer</a></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="main-wrap">
                    <div id="page">
                        <div id="main-bot">
                            <div id="main-inner">
                                <div id="main">
                                    <decorator:body/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="footer-wrap">
        <div id="page">
            <jsp:include page="/WEB-INF/jsp/${systemLocale}/footer.jsp" />
        </div>
    </div>
</body>

<!-- Google Tracking -->
<script type="text/javascript">
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-34980612-1']);
_gaq.push(['_trackPageview']);

(function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript';
    ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(ga, s);
})();
</script>

</html>