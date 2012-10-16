<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/script/search.js"></script>
    <script type="text/javascript" src="${resources}/script/home.js"></script>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css"/>

    <title>LlamaryComer | Pide comida online - Barcelona and Madrid</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="main-content">
        <div class="butler-main">
            <div class="searchbar-wrapper">
                <div class="searchbar-location unselectable">Restaurants en tu zona</div>
                <div class="search-location-form">
                    <div class="location-input"><input class="location" type="text" id="loc" placeholder="<message:message key="search.watermark"/>"/></div>
                    <div class="location-button"><div class="search-container unselectable"><a class="search">Buscar</a></div></div>
                </div>
                <div class="location-direct unselectable">O entra directament en: Madrid / Barcelona</div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>
