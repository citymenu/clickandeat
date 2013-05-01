<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:set var="path" value="${fn:substringAfter(pageContext.request.servletPath,'/WEB-INF/jsp/')}"/>

<!doctype html>

<head>
    <meta name="description" content="<message:message key="page.description" escape="false"/>"/>

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
                        <div id="speech1" class="active">Bienvenido, deja que te ensena la manera más sencilla de pedir comida a domicilio...</div>
                        <div id="speech2" class="inactive">Disfruta de la gran variedad de cocinas que ponemos a tu alcance: italiana, mejicana, china, pizza, y mucho más.</div>
                        <div id="speech3" class="inactive">Dime dónde hay que llevarla y te ayudaré a pedir tu comida ahora mismo...</div>
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
                                    más de 1000 restaurantes para elegir<br>
                                    es muy fácil pedir online<br>
                                    servicio a domicilio "en menos de" 45 minutos
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
                                    <img class="rounded-img-small" src="${resources}/images/restaurant/${restaurant.imageName}" width="65" height="65" alt="<util:escape value="${restaurant.name}"/>"/>
                                </td>
                                <td width="212">
                                    <c:if test="${restaurant.hasDiscounts == false}">
                                        <div class="restaurant-padding"></div>
                                    </c:if>
                                    <div class="restaurant-name"><util:escape value="${restaurant.name}"/></div>
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
                                        <c:if test="${deliveryOptions.minimumOrderForDelivery != null && deliveryOptions.minimumOrderForDelivery != null}">
                                            <span>
                                                (<message:message key="restaurant.minimum-delivery-order-short" escape="false"/> ${deliveryOptions.formattedMinimumOrderForDelivery} <span class="euro"><message:message key="config.currency" escape="false"/></span>)
                                            </span>
                                        </c:if>
                                        <c:if test="${deliveryOptions.allowFreeDelivery == true && deliveryOptions.minimumOrderForFreeDelivery != null && deliveryOptions.minimumOrderForFreeDelivery != 0}">
                                            <c:if test="${deliveryOptions.allowDeliveryBelowMinimumForFreeDelivery == false}">
                                                <span>
                                                    <message:message key="restaurant.free-delivery-short" escape="false"/>
                                                    (<message:message key="restaurant.minimum-delivery-order-short" escape="false"/> ${deliveryOptions.formattedMinimumOrderForFreeDelivery} <span class="euro"><message:message key="config.currency" escape="false"/>)
                                                </span>
                                            </c:if>
                                        </c:if>
                                    </div>
                                    <c:if test="${restaurant.hasDiscounts == true}">
                                        <div class="restaurant-discount-details">
                                            <div class="restaurant-discount-details-inner">
                                                <div class="discount-details">
                                                    <c:choose>
                                                        <c:when test="${restaurant.firstDiscount.type == 'FREE_ITEM'}">
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
