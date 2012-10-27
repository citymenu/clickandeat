<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/speechbubble.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css" charset="utf-8"/>

    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.carousel.js" charset="utf-8"></script>
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
                <div class="location-direct unselectable">Or search directly in: <a class="location" href="${ctx}/app/find-takeaway-food-in-london/loc/London"/>London</a></div>
                <div id="search-warning"><message:message key="search.location-not-found"/></div>
            </div>
        </div>
        <div class="recommendations">
            <p class="triangle-isosceles left">Why not try something from one of our recommended restaurants?</p>
        </div>
        <div id="carousel">
            <div class="carousel-items">
                <ul>
                    <c:forEach var="restaurant" items="${recommendations}">
                    <li>
                        <div class="restaurant-panel">
                            <a href="${ctx}/${restaurant.url}" class="blank" title="<util:escape value="${restaurant.name}"/>">
                                <img src="${resources}/images/restaurant/${restaurant.imageName}" height="65" alt="<util:escape value="${restaurant.name}"/>"/>
                                <div class="restaurant-name"><util:escape value="${restaurant.name}"/></div>
                                <div class="restaurant-summary"><util:escape value="${restaurant.address.town}"/> - <util:escape value="${restaurant.cuisineSummary}"/> sdfa asdf ;lksajdf ;lkjas df</div>
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