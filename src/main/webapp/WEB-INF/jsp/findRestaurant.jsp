<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/speechbubble.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/content.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/findrestaurant.css" charset="utf-8"/>

    <script type="text/javascript" src="${resources}/script/findrestaurant.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/googlemap.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/validation/validators_${systemLocale}.js" charset="utf-8"></script>
    <script type="text/javascript">var address="${address}";</script>
    <title><message:message key="page-title.search-results" escape="false"/></title>
</head>

<body>
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
    <div id="content">
        <div class="content-wrapper">
            <table width="1000">
                <tr valign="top">
                    <!-- Filter -->
                    <td width="220">
                        <div id="filter-wrapper">
                            <div id="filter">
                                <div class="filter-wrapper">
                                    <span class="location-title"><message:message key="location.your-location"/></span>
                                    <span class="location-link">
                                        <a class="restaurant-location" href="javascript:locationEdit();"><message:message key="button.change"/></a>
                                    </span>
                                    <div class="location-summary">
                                        <c:choose>
                                            <c:when test="${search == null || search.addressSummary == null}">
                                                <message:message key="location.not-set"/>
                                            </c:when>
                                            <c:otherwise>
                                                ${search.addressSummary}
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="filter-wrapper" style="margin-top:10px;">
                                    <div class="filter-title"><message:message key="search.select-cuisine"/>:</div>
                                    <div class="filter-cuisine-list">
                                        <div class="filter-cuisine">
                                            <c:choose>
                                                <c:when test="${cuisine == ''}">
                                                    <label><input type="radio" name="cuisine" value="" class="search-checkbox" checked/><span class="cuisine-title selectedcuisine"><message:message key="search.all"/></span></label>
                                                </c:when>
                                                <c:otherwise>
                                                    <label><input type="radio" name="cuisine" value="" class="search-checkbox"/><span class="cuisine-title"><message:message key="search.all"/></span></label>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <c:forEach var="entry" items="${cuisineCount}">
                                            <div class="filter-cuisine">
                                                <c:choose>
                                                    <c:when test="${entry.key == cuisine}">
                                                        <label><input type="radio" name="cuisine" value="${entry.key}" class="search-checkbox" checked/><span class="cuisine-title selectedcuisine">${entry.key} (${entry.value})</span></label>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <label><input type="radio" name="cuisine" value="${entry.key}" class="search-checkbox"/><span class="cuisine-title">${entry.key} (${entry.value})</span></label>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="filter-wrapper" style="margin-top:10px;">
                                    <div class="filter-additional" >
                                        <label><input type="checkbox" id="ignore-closed"><span class="cuisine-title" id="filterclosed"><message:message key="search.ignore-closed"/></span></label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </td>
                    <!-- Search results -->
                    <td width="780">
                        <c:choose>
                            <c:when test="${count == 0}">
                                <jsp:include page="/WEB-INF/jsp/${systemLocale}/noResultsFound.jsp" />
                            </c:when>
                            <c:otherwise>
                                <div class="search-results-header">
                                    <div class="search-location-results"><message:message key="search.restaurants-serving-your-location" format="${count}"/></div>
                                </div>
                            </c:otherwise>
                        </c:choose>


                        <c:set var="phoneOrdersOnlyflag" value="0"/>

                        <c:forEach var="restaurant" items="${results}">
                            <c:if test="${restaurant.phoneOrdersOnly == true}">
                                <c:if test="${phoneOrdersOnlyflag=='0'}">
                                    <c:set var="phoneOrdersOnlyflag" value="1"/>
                                    <div class="phone-orders-wrapper">
                                        <div class="phone-orders-header"><message:message key="search.phone-orders-only-text"/></div>
                                    </div>
                                </c:if>
                            </c:if>

                            <div class="result-wrapper" isOpen="${restaurant.open}" cuisines="${restaurant.cuisineSummary}" isPhoneOnly="${restaurant.phoneOrdersOnly}">
                                <div class="result" url="${restaurant.url}" type="link">
                                    <table width="780">
                                        <tr valign="middle">
                                            <td width="105">
                                                <div class="restaurant-image">
                                                    <img src="${resources}/images/restaurant/${restaurant.imageName}" width="85" alt="<util:escape value="${restaurant.name}"/>"/>
                                                </div>
                                            </td>
                                            <td width="200">
                                                <div class="table-entry">
                                                    <h2 class="restaurant-name"><util:escape value="${restaurant.name}"/></h2>
                                                    <div class="cuisine-summary"><util:escape value="${restaurant.cuisineSummary}"/></div>
                                                    <c:if test="${restaurant.hasDiscounts == true}">
                                                        <div class="restaurant-discount-details">
                                                            <div class="scissors"></div>
                                                            <div class="discount-details"><util:escape value="${restaurant.firstDiscount.title}"/></div>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </td>
                                            <td width="180">
                                                <div class="table-entry">
                                                    <div class="address-details">
                                                        <util:escape value="${restaurant.address.summary}"/>
                                                        <div class="location-details">
                                                            <c:if test="${search != null && search.coordinates != null}">
                                                                (<message:message key="search.distance"/>: ${restaurant.formattedDistanceToSearchLocation}km)
                                                            </c:if>
                                                        </div>
                                                        <div class="restaurant-telephone"><message:message key="restaurant.contact"/>: ${restaurant.contactTelephone}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td width="165">
                                                <div class="restaurant-opening-details">
                                                    <div class="opening-details">${restaurant.todaysOpeningTimes}</div>
                                                        <c:choose>
                                                            <c:when test="${restaurant.collectionOnly}">
                                                                <div class="delivery-details">
                                                                    <message:message key="restaurant.collection-only"/>
                                                                </div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:set var="deliveryOptions" value="${restaurant.deliveryOptions}"/>
                                                                <div class="delivery-details">
                                                                    <c:if test="${deliveryOptions.minimumDeliveryCharge == deliveryOptions.maximumDeliveryCharge && (deliveryOptions.minimumDeliveryCharge == '0.00' || deliveryOptions.minimumDeliveryCharge == '0,00')}">
                                                                        <div><message:message key="restaurant.free-delivery" escape="false"/></div>
                                                                    </c:if>
                                                                    <c:if test="${deliveryOptions.minimumDeliveryCharge == deliveryOptions.maximumDeliveryCharge && deliveryOptions.minimumDeliveryCharge != '0.00' && deliveryOptions.minimumDeliveryCharge != '0,00'}">
                                                                        <div>${deliveryOptions.minimumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span></div>
                                                                    </c:if>
                                                                    <c:if test="${deliveryOptions.minimumDeliveryCharge != deliveryOptions.maximumDeliveryCharge}">
                                                                        <div>
                                                                            ${deliveryOptions.minimumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span> -
                                                                            ${deliveryOptions.maximumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span>
                                                                        </div>
                                                                    </c:if>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </td>
                                            <td width="140" align="right">
                                                <div class="menu-link">
                                                    <c:choose>
                                                        <c:when test="${restaurant.open == true}">
                                                            <c:choose>
                                                                <c:when test="${restaurant.phoneOrdersOnly == true}">
                                                                    <div class="button-green">
                                                                        <div class="button-text"><message:message key="search.call-now"/></div>
                                                                    </div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="button-green" url="${restaurant.url}" type="link">
                                                                        <div class="button-text"><message:message key="search.order-now"/></div>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:choose>
                                                                <c:when test="${restaurant.phoneOrdersOnly == true}">
                                                                    <div class="button-orange">
                                                                        <div class="button-text"><message:message key="search.view-menu"/></div>
                                                                    </div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="button-orange">
                                                                        <div class="button-text"><message:message key="search.pre-order"/></div>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </c:forEach>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</body>
</html>

