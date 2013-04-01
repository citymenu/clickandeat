<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:set var="path" value="${fn:substringAfter(pageContext.request.servletPath,'/WEB-INF/jsp/')}"/>

<!doctype html>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/speechbubble.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css" charset="utf-8"/>

    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.ui-1.9.2-min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.carousel.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/home.js" charset="utf-8"></script>
    <script type="text/javascript">var watermark="<message:message key="search.watermark"/>";</script>
    <script type="text/javascript">var notfound = '${notfound}';</script>

    <title>Comida a domicilio | Restaurantes on line en Llamarycomer.com</title>
    <meta name="keywords" content="comida a domicilio - comida en casa - restaurantes on line - comida para llevar - llamar y comer" />
    <meta name="description" content="Llamar y Comer es la manera más fácil de pedir comida a domicilio y para llevar. Una gran variedad de restaurantes a tu disposición" />
    <meta http-equiv="Content-Language" content="es"/>

</head>

<body>
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
    <div id="banner">
        <div id="banner-wrap">
        <div id="banner-outer">
            <div id="banner-inner">
                <div class="wsite-header">
                    <div id="butler"></div>
                    <div id="speechbubble">
                        <div id="speech1" class="active">Hi, I&apos;m here to show you the easiest way to get local takeaway food delivered to your home....</div>
                        <div id="speech2" class="inactive">I have a wide range of restaurant menus to suit all tastes. Italian, Mexican, Chinese, Pizza and many more....</div>
                        <div id="speech3" class="inactive">Tell me your location in the box below and I&apos;ll help you order local takeaway food right now....</div>
                    </div>
                    <div id="bannercarouselwrapper">
                        <div id="bannercarousel">
                            <div class="carousel-items">
                                <ul>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/pizza.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">Pizza</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/chinese.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">China</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/sushi.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">Sushi</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/mexican.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">Mexicana</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/mediterranean.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">Mediterránea</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/american.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">Americana</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/asian.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">Asiática</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/salad.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">Ensaladas</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/vegetarian.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon">Vegetariana</div>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div id="searchbar">
                        <div id="searchbarleft">
                            <div class="searchbar">
                                <table width="406">
                                    <tr valign="middle">
                                        <td width="302">
                                            <div class="location-input">
                                                <input class="location" type="text" id="loc" value="${address}" placeholder=""/>
                                            </div>
                                        </td>
                                        <td width="104">
                                            <div class="search-button">
                                                <div class="search-button-text">Buscar</div>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <div class="searchbartext">
                                O entra directamente en: <a class="location" href="${ctx}/app/comida-a-domicilio-en-madrid/loc/Madrid"/>Madrid</a> / <a class="location" href="${ctx}/app/comida-a-domicilio-en-barcelona/loc/Barcelona"/>Barcelona</a>
                            </div>
                        </div>
                        <div id="searchbarright">
                            <div class="searchbarinfo">
                                <div>
                                    OVER 500 RESTAURANTES LISTED<br>
                                    SUPER EASY ORDERING PROCESS<br>
                                    HOME DELIVERY WITHIN 45 MINUTES
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="content">
        <div id="wsite-content" class="wsite-elements wsite-not-footer">

            <div class="home-content-header">¿Por qué no pruebas uno de nuestros restaurantes recomendados?</div>
            <div id="carousel" style="margin-top:30px;">
                <div class="carousel-items">
                    <ul>
                        <c:forEach var="restaurant" items="${recommendations}" varStatus="status">
                        <c:if test="${status.count % 2 == 1}">
                        <li>
                        </c:if>

                        <c:choose>
                            <c:when test="${status.count %2 == 1}">
                            <div class="restaurant-panel-left">
                            </c:when>
                            <c:otherwise>
                            <div class="restaurant-panel-right">
                            </c:otherwise>
                        </c:choose>

                        <div class="divider"></div>

                        <table width="430">
                            <tr valign="top">
                                <td width="78" align="left">
                                    <a class="blank" href="${restaurant.url}">
                                        <img src="${resources}/images/restaurant/${restaurant.imageName}" width="65" height="65" alt="<util:escape value="${restaurant.name}"/>"/>
                                    </a>
                                </td>
                                <td width="210">
                                    <a class="blank" href="${restaurant.url}">
                                        <div class="restaurant-name"><util:escape value="${restaurant.name}"/></div>
                                    </a>
                                    <div class="restaurant-summary"><util:escape value="${restaurant.address.town}"/> - <util:escape value="${restaurant.cuisineSummary}"/></div>
                                    <div class="opening-details"><message:message key="search.open-today"/>: ${restaurant.todaysOpeningTimes}</div>
                                </td>
                                <td width="142" align="right">
                                    <c:if test="${restaurant.hasDiscounts == true}">
                                        <div class="restaurant-discount-details">
                                            <div class="scissors"></div>
                                            <div class="discount-details"><util:escape value="${restaurant.firstDiscount.title}"/></div>
                                        </div>
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                        </div>

                        <c:if test="${status.count % 2 == 0}">
                        </li>
                        </c:if>
                        </c:forEach>
                    </ul>
                </div>
            </div>

        </div>
    </div>
</body>
