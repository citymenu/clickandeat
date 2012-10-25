<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css" charset="utf-8"/>

    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.carousel.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/home.js" charset="utf-8"></script>
    <script type="text/javascript">var watermark="<message:message key="search.watermark"/>";</script>
    <script type="text/javascript">var notfound = '${notfound}';</script>

    <title>LlamaryComer | Pide comida online - Barcelona y Madrid</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="main-content">
        <div class="butler-main">
            <div class="searchbar-wrapper">
                <div class="searchbar-location unselectable">Restaurantes en tu zona</div>
                <div class="search-location-form">
                    <div class="location-input"><input class="location" type="text" id="loc" placeholder=""/></div>
                    <div class="location-button"><div class="search-container unselectable"><a class="search">Buscar</a></div></div>
                </div>
                <div class="location-direct unselectable">O entra directament en: <a class="location" href="${ctx}/app/find-takeaway-food-in-madrid/loc/Matrid"/>Madrid</a> / <a class="location" href="${ctx}/app/find-takeaway-food-in-barcelona/loc/Barcelona"/>Barcelona</a></div>
                <div id="search-warning"><message:message key="search.location-not-found"/></div>
            </div>
        </div>
        <div id="carousel">
            <h2>Why not try something from one of our recommended restaurants?</h2>
            <div class="carousel-items">
                <ul>
                    <c:forEach var="restaurant" items="${recommendations}">
                    <li>
                        <div class="restaurant-panel">
                            <a href="${ctx}/${restaurant.url}" class="blank" title="<util:escape value="${restaurant.name}"/>">
                                <img src="${resources}/images/restaurant/${restaurant.imageName}" height="75" alt="<util:escape value="${restaurant.name}"/>"/>
                                <div class="restaurant-name"><util:escape value="${restaurant.name}"/></div>
                                <div class="restaurant-summary"><util:escape value="${restaurant.address.town}"/> - <util:escape value="${restaurant.cuisineSummary}"/></div>
                            </a>
                        </div>
                    </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/${systemLocale}/footer.jsp" />

</body>
</html>
