<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css" charset="utf-8"/>

    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/script/home.js" charset="utf-8"></script>
    <script type="text/javascript">var watermark="<message:message key="search.watermark"/>";</script>
    <script type="text/javascript">var notfound = '${notfound}';</script>

    <title>LlamaryComer | Order Takeaway Food Online - London</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="main-content">
        <div class="butler-main">
            <div class="searchbar-wrapper">
                <div class="searchbar-location unselectable">Restaurants in your area</div>
                <div class="search-location-form">
                    <div class="location-input"><input class="location" type="text" id="loc" value="${address}" placeholder=""/></div>
                    <div class="location-button"><div class="search-container unselectable"><a class="search">Buscar</a></div></div>
                </div>
                <div class="location-direct unselectable">Or search directly in: <a class="home" id="london">London</a></div>
                <div id="search-warning"><message:message key="search.location-not-found"/></div>
            </div>
        </div>
        <div class="box-wrapper">
            <table width="980">
                <tr valign="top">
                    <td width="530">
                        <div class="home-caption">
                            <h2>Simply the easiest way to order fast food online</h2>
                            <p class="triangle-isosceles left">But it could be any element you want.</p>
                        </div>
                    </td>
                    <td width="450">
                        <div class="box-large">
                            <h2>Now serving the following locations</h2>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/${systemLocale}/footer.jsp" />

</body>
</html>