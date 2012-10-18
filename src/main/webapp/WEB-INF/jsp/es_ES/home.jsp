<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/script/home.js"></script>
    <script type="text/javascript">var watermark="<message:message key="search.watermark"/>";</script>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css"/>

    <title>LlamaryComer | Pide comida online - Barcelona y Madrid</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="main-content">
        <div class="butler-main">
            <div class="searchbar-wrapper">
                <div class="searchbar-location unselectable">Restaurants en tu zona</div>
                <div class="search-location-form">
                    <div class="location-input"><input class="location" type="text" id="loc" placeholder=""/></div>
                    <div class="location-button"><div class="search-container unselectable"><a class="search">Buscar</a></div></div>
                </div>
                <div class="location-direct unselectable">O entra directament en: <a class="home" id="madrid">Madrid</a> / <a class="home" id="barcelona">Barcelona</a></div>
                <div id="search-warning"><message:message key="search.location-not-found"/></div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>
