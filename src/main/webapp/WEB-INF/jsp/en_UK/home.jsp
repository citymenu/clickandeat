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
            <div id="searchbar-wrapper">
                <div class="triangle-right left">
                    <div class="searchbar-content">
                        <div class="searchbar-location unselectable">Find restaurants in your area</div>
                        <div class="search-location-form">
                            <div class="location-input"><input class="location" type="text" id="loc" value="${address}" placeholder=""/></div>
                            <div class="location-button"><div class="search-container unselectable"><a class="search">Buscar</a></div></div>
                        </div>
                        <div class="location-direct unselectable">Browse restaurants in: <a class="location" href="${ctx}/app/find-takeaway-food-in-london/loc/London"/>London</a></div>
                        <div id="search-warning"><message:message key="search.location-not-found"/></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="box-large-middle">
            <div class="caption">Or why not try one of our recommended restaurants?</div>
            <div id="carousel">
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

                        <table width="458">
                            <tr valign="top">
                                <td width="78" align="left">
                                    <a class="blank" href="${restaurant.url}">
                                        <img src="${resources}/images/restaurant/${restaurant.imageName}" width="65" height="65" alt="<util:escape value="${restaurant.name}"/>"/>
                                    </a>
                                </td>
                                <td width="220">
                                    <a class="blank" href="${restaurant.url}">
                                        <div class="restaurant-name"><util:escape value="${restaurant.name}"/></div>
                                    </a>
                                    <div class="restaurant-summary"><util:escape value="${restaurant.address.town}"/> - <util:escape value="${restaurant.cuisineSummary}"/></div>
                                    <div class="opening-details"><message:message key="search.open-today"/>: ${restaurant.todaysOpeningTimes}</div>
                                </td>
                                <td width="150" align="right">
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

        <div class="box-large-bottom">
            <div class="caption">Simply the easiest way to order takeaway food online</div>
            <div class="help">
                <table width="976">
                    <tr>
                        <td width="244" align="center">
                            <div class="step step-1">
                                <div class="step-number">1.</div>
                                <div class="step-detail">Tell us where you are</div>
                            </div>
                        </td>
                        <td width="244" align="center">
                            <div class="step step-2">
                                <div class="step-number">2.</div>
                                <div class="step-detail">Browse menus from our wide range of restaurants</div>
                            </div>
                        </td>
                        <td width="244" align="center">
                            <div class="step step-3">
                                <div class="step-number">3.</div>
                                <div class="step-detail">Build and pay for your order</div>
                            </div>
                        </td>
                        <td width="244" align="center">
                            <div class="step step-4">
                                <div class="step-number">4.</div>
                                <div class="step-detail">Your food is on its way</div>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/${systemLocale}/footer.jsp" />

</body>
</html>