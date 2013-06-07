<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:set var="path" value="${fn:substringAfter(pageContext.request.servletPath,'/WEB-INF/jsp/')}"/>

<!doctype html>

<head>
    <title>Comida a domicilio | Restaurantes on line en Llamarycomer.com</title>
    <meta name="description" content="Llamar y Comer es la manera más fácil de pedir comida a domicilio y para llevar. Una gran variedad de restaurantes a tu disposición">
    <meta http-equiv="Content-Language" content="es">

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/speechbubble.css" charset="utf-8">
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/home.css" charset="utf-8">

    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&amp;libraries=places&amp;language=<locale:language/>&amp;sensor=false"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.ui-1.9.2-min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/jquery/script/jquery.carousel.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/home.js" charset="utf-8"></script>
    <script type="text/javascript">var watermark="<message:message key="search.watermark"/>";</script>
    <script type="text/javascript">var notfound = '${notfound}';</script>
</head>

<body>
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
    <div style="display:none">
        <h1><strong>Comida a domicilio en Madrid, Barcelona</strong></h1>
        <p>Llamarycomer cuenta con más de 1.000 restaurantes listos para entregar alimentos a usted en su casa</p>
    </div>

    <!-- SEO entry point -->
    <div style="display:none">
        <c:forEach var="location" items="${locationprimary}" varStatus="status">
            <div><a href="${ctx}/app/comida-domicilio-${location.first}/loc/${location.first}">seo${status.count}</a></div>
        </c:forEach>
    </div>

    <div id="banner">
        <div id="banner-wrap">
        <div id="banner-outer">
            <div id="banner-inner">
                <div class="wsite-header">
                    <div id="butler"></div>
                    <div id="speechbubble">
                        <div id="speech1" class="active unselectable">Bienvenido, deja que te ensena la manera más sencilla de pedir comida a domicilio...</div>
                        <div id="speech2" class="inactive unselectable">Disfruta de la gran variedad de cocinas que ponemos a tu alcance: italiana, mejicana, china, pizza, y mucho más.</div>
                        <div id="speech3" class="inactive unselectable">Dime dónde hay que llevarla y te ayudaré a pedir tu comida ahora mismo...</div>
                    </div>
                    <div id="bannercarouselwrapper">
                        <div id="bannercarousel" style="visibility:hidden">
                            <div class="carousel-items">
                                <ul>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/pizza.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">Pizza</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/chinese.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">China</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/sushi.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">Sushi</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/mexican.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">Mexicana</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/mediterranean.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">Mediterránea</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/american.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">Americana</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/asian.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">Asiática</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/salad.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">Ensaladas</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="rounded-img-wrapper">
                                            <div class="rounded-img" style="background:url(${resources}/images/food/vegetarian.jpg) no-repeat center center;">
                                                <div class="rounded-img-ribbon unselectable">Vegetariana</div>
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
                                                <input class="location" type="text" id="loc" value="${address}" placeholder="">
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
                                O entra directamente en: <a class="location" href="${ctx}/app/comida-domicilio-madrid/loc/madrid">Madrid</a> / <a class="location" href="${ctx}/app/comida-domicilio-barcelona/loc/barcelona">Barcelona</a>
                            </div>
                        </div>
                        <div id="searchbarright">
                            <div class="searchbarinfo">
                                <h2>más de 1000 restaurantes para elegir</h2>
                                <h2>es muy fácil pedir online</h2>
                                <h2>servicio a domicilio "en menos de" 45 minutos</h2>
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
                    <c:forEach var="restaurant" items="${recommendations}" varStatus="status">
                        <c:choose>
                            <c:when test="${status.count %3 == 1}">
                            <div class="restaurant-panel-left" url="${restaurant.url}" type="recommendation">
                            </c:when>
                            <c:when test="${status.count %3 == 2}">
                            <div class="restaurant-panel-center" url="${restaurant.url}" type="recommendation">
                            </c:when>
                            <c:otherwise>
                            <div class="restaurant-panel-right" url="${restaurant.url}" type="recommendation">
                            </c:otherwise>
                        </c:choose>

                        <div class="divider"></div>

                        <table width="280">
                            <tr valign="top">
                                <td width="78" align="left">
                                    <a href="${restaurant.url}" class="inherit">
                                        <img class="rounded-img-small" src="${resources}/images/restaurant/${restaurant.imageName}" width="65" height="65" alt="${restaurant.imageAlt}">
                                    </a>
                                </td>
                                <td width="212">
                                    <c:if test="${restaurant.hasDiscounts == false}">
                                        <div class="restaurant-padding"></div>
                                    </c:if>
                                    <div class="restaurant-name"><a href="${restaurant.url}" class="inherit"><util:escape value="${restaurant.name}"/></a></div>
                                    <div class="restaurant-summary"><util:escape value="${restaurant.address.town}"/> - <util:escape value="${restaurant.cuisineSummary}"/></div>
                                    <c:set var="deliveryOptions" value="${restaurant.deliveryOptions}"/>
                                    <div class="delivery-details">
                                        <c:if test="${deliveryOptions.minimumDeliveryCharge == deliveryOptions.maximumDeliveryCharge && deliveryOptions.minimumOrderForFreeDelivery == 0 && (deliveryOptions.minimumDeliveryCharge == '0.00' || deliveryOptions.minimumDeliveryCharge == '0,00')}">
                                            <span><message:message key="restaurant.free-delivery-short" escape="false"/></span>
                                        </c:if>
                                        <c:if test="${deliveryOptions.minimumDeliveryCharge == deliveryOptions.maximumDeliveryCharge && deliveryOptions.minimumDeliveryCharge != '0.00' && deliveryOptions.minimumDeliveryCharge != '0,00'}">
                                            <span>
                                                ${deliveryOptions.minimumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span>
                                            </span>
                                        </c:if>
                                        <c:if test="${deliveryOptions.minimumDeliveryCharge != deliveryOptions.maximumDeliveryCharge}">
                                            <span>
                                                ${deliveryOptions.minimumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span> -
                                                ${deliveryOptions.maximumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span>
                                            </span>
                                        </c:if>
                                        <c:if test="${deliveryOptions.minimumOrderForDelivery != null && deliveryOptions.minimumOrderForDelivery != 0 && deliveryOptions.minimumOrderForDelivery != deliveryOptions.minimumOrderForFreeDelivery}">
                                            <span>
                                                (<message:message key="restaurant.minimum-delivery-order-short" escape="false"/> ${deliveryOptions.formattedMinimumOrderForDelivery} <span class="euro"><message:message key="config.currency" escape="false"/></span>)
                                            </span>
                                        </c:if>
                                        <c:if test="${deliveryOptions.allowFreeDelivery == true && deliveryOptions.minimumOrderForFreeDelivery != null && deliveryOptions.minimumOrderForFreeDelivery != 0}">
                                            <c:if test="${deliveryOptions.allowDeliveryBelowMinimumForFreeDelivery == false}">
                                                <span>
                                                    <message:message key="restaurant.free-delivery-short" escape="false"/>
                                                    (<message:message key="restaurant.minimum-delivery-order-short" escape="false"/> ${deliveryOptions.formattedMinimumOrderForFreeDelivery} <span class="euro"><message:message key="config.currency" escape="false"/></span>)
                                                </span>
                                            </c:if>
                                        </c:if>
                                    </div>
                                    <c:if test="${restaurant.hasDiscounts == true}">
                                        <div class="restaurant-discount-details">
                                            <div class="restaurant-discount-details-inner">
                                                <div class="discount-details">
                                                    <c:choose>
                                                        <c:when test="${restaurant.firstDiscount.discountType == 'DISCOUNT_FREE_ITEM'}">
                                                            <message:message key="restaurant.discount.free-item"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <message:message key="restaurant.discount.short" format="${restaurant.firstDiscount.amountSummary}"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

</body>
