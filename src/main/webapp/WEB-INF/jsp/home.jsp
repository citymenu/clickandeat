<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css"/>
    <script type="text/javascript" src="${resources}/script/search.js"></script>
    <script type="text/javascript" src="${resources}/script/home.js"></script>
    <title><message:message key="page-title.home" escape="false"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <div class="header-tagline">
            <h1><message:message key="home.tagline" escape="false"/></h1>
        </div>
        <div class="searchbar-wrapper">
            <div class="searchbar">
                <div class="outer-box">
                    <div class="middle-box">
                        <div class="center-box">
                            <div class="searchelement-wrapper">
                                <div class="search-input">
                                    <h2><message:message key="search.where-are-you"/></h2>
                                    <input class="location" type="text" id="loc" placeholder="<message:message key="search.watermark"/>"/>
                                </div>
                                <div class="search-button">
                                    <input class="searchbutton" type="button" id="searchbutton" value="<message:message key="button.search"/>"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>
