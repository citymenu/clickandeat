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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"><html xmlns="http://www.w3.org/1999/xhtml" lang="<locale:language/>">

<head>
    <meta charset="utf-8"/>
    <meta name="robots" content="all" />
    <meta http-equiv="expires" content="0"/>
    <meta name="description" content="<message:message key="page.description" escape="false"/>"/>
    <%@ include file="/WEB-INF/jsp/taglibs_js.jsp" %>
	<link rel="shortcut icon" href="${resources}/images/favicon.png">

    <!-- Typekit -->
    <script type="text/javascript" src="//use.typekit.net/iwp4tpg.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>

    <!-- Stylesheets -->
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/MyFontsWebfontsKit.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/main.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/header.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/content.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/footer.css"/>
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/dialog.css"/>

    <!-- JQuery -->
    <script type="text/javascript" src="${resources}/jquery/script/jquery-1.8.2.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.mousewheel-3.0.6.pack.js"charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.scrollto.js" charset="utf-8"></script>

    <!-- Fancybox -->
    <link rel="stylesheet" href="${resources}/fancybox/source/jquery.fancybox.css" type="text/css" media="screen" charset="utf-8"/>
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
    <link rel="stylesheet" type="text/css" media="all" charset="utf-8" href="${resources}/css/overrides.css"/>

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


<body>
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
                                <div class="header-company-text unselectable"><a href="${ctx}/home.html" class="blank">llamar<span class="header-company-small">y</span>comer</a></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="main-wrap">

                    <div id="nav-wrap">
                        <div id="page">
                            <div id="topnav">
                                <div class="navigation-links">
                                    <ul>
                                        <c:choose>
                                            <c:when test="${path == 'en_UK/home.jsp' || path == 'es_ES/home.jsp'}">
                                                <li class="active unselectable"><message:message key="workflow.1-enter-your-location"/></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="unselectable"><a href="${ctx}/home.html"><message:message key="workflow.1-enter-your-location"/></a></li>
                                            </c:otherwise>
                                        </c:choose>

                                        <li class="arrow">&gt&gt</li>

                                        <c:choose>
                                            <c:when test="${path == 'findRestaurant.jsp'}">
                                                <li class="active unselectable"><message:message key="workflow.2-select-a-restaurant"/></li>
                                            </c:when>
                                            <c:when test="${search != null}">
                                                <li class="unselectable"><a href="${ctx}/app/<message:message key="url.find-takeaway"/>/session/loc"><message:message key="workflow.2-select-a-restaurant"/></a></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="unselectable"><message:message key="workflow.2-select-a-restaurant"/></li>
                                            </c:otherwise>
                                        </c:choose>

                                        <li class="arrow">&gt&gt</li>

                                        <c:choose>
                                            <c:when test="${path == 'restaurant.jsp' && orderrestaurantid != null && restaurantid != null && orderrestaurantid != restaurantid && search != null}">
                                                <li class="active unselectable"><a href="${ctx}/app/restaurant/${orderrestaurantid}"><message:message key="workflow.3-build-your-order"/></a></li>
                                            </c:when>
                                            <c:when test="${path == 'restaurant.jsp'}">
                                                <li class="active unselectable"><message:message key="workflow.3-build-your-order"/></li>
                                            </c:when>
                                            <c:when test="${orderrestaurantid != null && search != null}">
                                                <li class="unselectable"><a href="${ctx}/app/restaurant/${orderrestaurantid}"><message:message key="workflow.3-build-your-order"/></a></li>
                                            </c:when>
                                            <c:when test="${restaurantid != null && search != null}">
                                                <li class="unselectable"><a href="${ctx}/app/restaurant/${restaurantid}"><message:message key="workflow.3-build-your-order"/></a></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="unselectable"><message:message key="workflow.3-build-your-order"/></li>
                                            </c:otherwise>
                                        </c:choose>

                                        <li class="arrow">&gt&gt</li>

                                        <c:choose>
                                            <c:when test="${path == 'checkout.jsp' || path == 'payment.jsp' || path == 'en_UK/callNowSummary.jsp' || path == 'es_ES/callNowSummary.jsp'}">
                                                <li class="active unselectable"><message:message key="workflow.4-checkout"/></li>
                                            </c:when>
                                            <c:when test="${cancheckout != null && cancheckout == true}">
                                                <li class="unselectable"><a href="${ctx}/checkout.html"><message:message key="workflow.4-checkout"/></a></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="unselectable"><message:message key="workflow.4-checkout"/></li>
                                            </c:otherwise>
                                        </c:choose>

                                    </ul>
                                </div>
                                <div style="clear:both"></div>
                            </div>
                        </div>
                    </div>

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


</body>

</html>