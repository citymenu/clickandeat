<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<c:set var="path" value="${fn:substringAfter(pageContext.request.servletPath,'/WEB-INF/jsp/')}"/>

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
    <script type="text/javascript">var path="${path}";</script>
</head>

<body>
    <div id="banner">
        <div id="banner-outer">
            <div id="banner-inner">
                <div class="wsite-header">
                    <div class="hero-border"></div>
                    <div class="hero-banner-upper">The easiest way to order takeaway food online</div>
                    <div class="hero-banner-main">Search for restaurants<br>in your area</div>
                    <div class="searchbar-container">
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
                                            <div class="search-button-text">Search</div>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                    <div class="hero-banner-lower">Or go directly to restaurants in <u>Madrid</u>/<u>Barcelona</u></div>
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

                        <table width="448">
                            <tr valign="top">
                                <td width="78" align="left">
                                    <a class="blank" href="${restaurant.url}">
                                        <img src="${resources}/images/restaurant/${restaurant.imageName}" width="65" height="65" alt="<util:escape value="${restaurant.name}"/>"/>
                                    </a>
                                </td>
                                <td width="228">
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
            <div class="home-content-header">La manera más cómoda de pedir comida a domicilio</div>
            <table width="932" style="margin:20px auto 10px auto;">
                <tr>
                    <td width="233" align="center">
                        <div class="step step-1">
                            <div class="step-number">1.</div>
                            <div class="step-detail">Dinos dónde estás</div>
                        </div>
                    </td>
                    <td width="233" align="center">
                        <div class="step step-2">
                            <div class="step-number">2.</div>
                            <div class="step-detail">Elige tu comida preferida</div>
                        </div>
                    </td>
                    <td width="233" align="center">
                        <div class="step step-3">
                            <div class="step-number">3.</div>
                            <div class="step-detail">Realiza tu pedido</div>
                        </div>
                    </td>
                    <td width="233" align="center">
                        <div class="step step-4">
                            <div class="step-number">4.</div>
                            <div class="step-detail">Tu comida está de camino</div>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</body>
