<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css"/>
    <script type="text/javascript" src="${resources}/script/home.js"></script>
    <script type="text/javascript" src="${resources}/script/search.js"></script>

    <!-- Bootstrap -->
    <script type="text/javascript" src="${resources}/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/bootstrap.css"/>

    <title><message:message key="page-title.home" escape="false"/></title>
    <script type="text/javascript">var watermark="<message:message key="search.watermark"/>";</script>
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
                                <div class="searchelement searchleft">
                                    <h2><message:message key="search.where-are-you"/></h2>
                                    <input class="location" type="text" id="loc"/>
                                </div>
                                <div class="searchelement button">
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
