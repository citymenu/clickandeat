<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&libraries=places&language=<locale:language/>&sensor=false"></script>

    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/speechbubble.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/content.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css" charset="utf-8"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/findrestaurant.css" charset="utf-8"/>

    <script type="text/javascript" src="${resources}/script/orders.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/findrestaurant.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/googlemap.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/validation/validators_${systemLocale}.js" charset="utf-8"></script>
    <script type="text/javascript">var address="${address}";</script>
    <title><message:message key="page-title.search-results" escape="false"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <!-- Search results -->
                <td width="760">
                    <div class="search-results-wrapper">
                        <div class="search-results-header-wrapper">
                            <h2><message:message key="search.search-results"/></h2>
                            <div class="search-location-wrapper">
                                <div class="search-location-edit">
                                    <c:choose>
                                        <c:when test="${search == null || search.location == null}">
                                            <div class="search-location"><message:message key="search.location-not-set"/> <a class="location-button" id="changeLocation"><message:message key="button.change"/></a></div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="search-location"><message:message key="search.your-location"/> "${search.location.displayAddress}" <a class="location-button" id="changeLocation"><message:message key="button.change"/></a></div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <c:choose>
                                    <c:when test="${address == '' && cuisine != ''}">
                                    <div class="search-location-results"><message:message key="search.restaurants-by-cuisine" format="${cuisine}"/></div>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="search-location-results"><message:message key="search.restaurants-serving-your-location" format="${count}"/></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="location-warning-wrapper">
                                <c:choose>
                                    <c:when test="${search != null && search.location != null && address == ''}">
                                        <div class="location-warning">
                                            <message:message key="search.showing-all-cuisine" format="${cuisine}"/>
                                            <br><a class="search-return" href="${ctx}/app/<message:message key="url.find-takeaway"/>/session/reloc"><message:message key="button.click-here"/></a>
                                            <message:message key="search.return-to-location"/>
                                        </div>
                                    </c:when>
                                    <c:when test="${search == null || search.location == null}">
                                        <div class="location-warning"><message:message key="search.location-not-set-warning"/></div>
                                    </c:when>
                                    <c:when test="${search.location.radiusWarning == true && count > 0}">
                                        <div class="location-warning"><message:message key="search.location-radius-warning"/></div>
                                    </c:when>
                                </c:choose>
                            </div>
                            <c:if test="${count > 0}">
                            <div class="search-filters">
                                <table width="690">
                                    <tr valign="middle">
                                        <td width="370">
                                            <div class="search-cuisine-filter">
                                                <message:message key="search.filter-by-cuisine"/>:
                                                <select class="search-select" id="cuisine-select">
                                                    <c:if test="${address != ''}">
                                                        <option value=""><message:message key="search.all"/></option>
                                                    </c:if>
                                                    <c:forEach var="entry" items="${cuisineCount}">
                                                        <c:choose>
                                                            <c:when test="${entry.key == cuisine}">
                                                                <option value="${entry.key}" selected><util:escape value="${entry.key}"/> (${entry.value})</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${entry.key}"><util:escape value="${entry.key}"/> (${entry.value})</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </td>
                                        <td width="320" align="right">
                                            <div class="search-cuisine-filter">
                                                <input type="checkbox" class="search-checkbox" id="ignore-closed"/> <message:message key="search.ignore-closed"/>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            </c:if>
                        </div>

                        <c:if test="${count == 0}">
                            <jsp:include page="/WEB-INF/jsp/${systemLocale}/noResultsFound.jsp" />
                        </c:if>

                        <c:if test="${count > 0}">
                        <div class="search-results-entry-wrapper">
                            <c:set var="phoneOrdersOnlyflag" value="0"/>
                            <c:forEach var="restaurant" items="${results}">
                                <c:if test="${restaurant.phoneOrdersOnly == true}">
                                    <c:if test="${phoneOrdersOnlyflag=='0'}">
                                        <c:set var="phoneOrdersOnlyflag" value="1"/>
                                        <div class="phone-orders-only-wrapper">
                                            <div class="phone-orders-only">
                                                <p class="triangle-isosceles left"><message:message key="search.phone-orders-only-text"/></p>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:if>
                                <div class="search-result-wrapper" isOpen="${restaurant.open}" cuisines="${restaurant.cuisineSummary}" isPhoneOnly="${restaurant.phoneOrdersOnly}">
                                    <div class="search-result">
                                        <table width="710">
                                            <tr valign="top">
                                                <td width="330">
                                                    <table width="330">
                                                        <tr valign="bottom">
                                                            <td width="85">
                                                                <a href="${ctx}/${restaurant.url}" class="blank">
                                                                    <c:if test="${restaurant.phoneOrdersOnly == true}">
                                                                    <div class="phone-orders-description"><message:message key="search.phone-orders-only"/></div>
                                                                    </c:if>
                                                                    <img src="${resources}/images/restaurant/${restaurant.imageName}" width="85" height="65" alt="<util:escape value="${restaurant.name}"/>"/>
                                                                </a>
                                                            </td>
                                                            <td width="245">
                                                                <div class="search-result-center">
                                                                    <h2><a href="${ctx}/${restaurant.url}" class="blank"><util:escape value="${restaurant.name}"/></a></h2>
                                                                    <div class="cuisine-summary"><util:escape value="${restaurant.cuisineSummary}"/></div>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td width="330" colspan="2">
                                                                <div class="address-details">
                                                                    <util:escape value="${restaurant.address.summary}"/><br>
                                                                    <a class="restaurant-location" onclick="showDirections(${restaurant.coordinates},null,null,'<util:escape value="${restaurant.name}" escapeComments="true"/>')"><message:message key="search.show-location"/></a>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                                <td width="380">
                                                    <table width="380">
                                                        <tr valign="top">
                                                            <td width="230">
                                                                <c:if test="${restaurant.hasDiscounts == true}">
                                                                    <div class="restaurant-discount-details">
                                                                        <div class="scissors"></div>
                                                                        <c:forEach var="discount" items="${restaurant.discounts}">
                                                                            <div class="discount-details"><util:escape value="${discount.title}"/></div>
                                                                        </c:forEach>
                                                                    </div>
                                                                </c:if>
                                                            </td>
                                                            <td width="150" align="right">
                                                                <div class="menu-link">
                                                                    <c:choose>
                                                                        <c:when test="${restaurant.open == true}">
                                                                            <c:choose>
                                                                                <c:when test="${restaurant.phoneOrdersOnly == true}">
                                                                                    <a href="${ctx}/${restaurant.url}" class="search-result-button-open"><message:message key="search.call-now"/></a>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <a href="${ctx}/${restaurant.url}" class="search-result-button-open"><message:message key="search.order-now"/></a>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <c:choose>
                                                                                <c:when test="${restaurant.phoneOrdersOnly == true}">
                                                                                    <a href="${ctx}/${restaurant.url}" class="search-result-button-closed"><message:message key="search.view-menu"/></a>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <a href="${ctx}/${restaurant.url}" class="search-result-button-closed"><message:message key="search.pre-order"/></a>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td width="380" colspan="2">
                                                                <div class="restaurant-opening-details">
                                                                    <div class="opening-details"><message:message key="search.open-today"/>: ${restaurant.todaysOpeningTimes}</div>
                                                                        <c:choose>
                                                                            <c:when test="${restaurant.collectionOnly}">
                                                                                <div class="delivery-details">
                                                                                    <message:message key="restaurant.collection-only"/>
                                                                                </div>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <c:set var="deliveryOptions" value="${restaurant.deliveryOptions}"/>
                                                                                <div class="delivery-details">
                                                                                    <div>MIN: ${deliveryOptions.minimumDeliveryCharge}</div>
                                                                                    <c:if test="${deliveryOptions.minimumDeliveryCharge == deliveryOptions.maximumDeliveryCharge && (deliveryOptions.minimumDeliveryCharge == '0.00' || deliveryOptions.minimumDeliveryCharge == '0,00')}">
                                                                                        <div><message:message key="restaurant.free-delivery" escape="false"/></div>
                                                                                    </c:if>
                                                                                    <c:if test="${deliveryOptions.minimumDeliveryCharge == deliveryOptions.maximumDeliveryCharge && deliveryOptions.minimumDeliveryCharge != '0.00' && deliveryOptions.minimumDeliveryCharge != '0,00'}">
                                                                                        <div><message:message key="restaurant.delivery-charge" escape="false"/> ${deliveryOptions.minimumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span></div>
                                                                                    </c:if>
                                                                                    <c:if test="${deliveryOptions.minimumDeliveryCharge != deliveryOptions.maximumDeliveryCharge}">
                                                                                        <div>
                                                                                            <message:message key="restaurant.delivery-charge" escape="false"/>
                                                                                            ${deliveryOptions.minimumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span> -
                                                                                            ${deliveryOptions.maximumDeliveryCharge} <span class="euro"><message:message key="config.currency" escape="false"/></span>
                                                                                        </div>
                                                                                    </c:if>
                                                                                    <c:if test="${deliveryOptions.allowFreeDelivery == true && deliveryOptions.minimumOrderForFreeDelivery != null && deliveryOptions.minimumOrderForFreeDelivery != 0}">
                                                                                        <div><message:message key="restaurant.minimum-order-for-free-delivery" escape="false"/> ${deliveryOptions.formattedMinimumOrderForFreeDelivery} <span class="euro"><message:message key="config.currency" escape="false"/></span></div>
                                                                                    </c:if>
                                                                                </div>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        </c:if>

                    </div>
                </td>

                <!-- Order panel -->
                <td width="260">
                    <div class="search-results-right">
                        <%@ include file="/WEB-INF/jsp/order.jsp" %>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/${systemLocale}/footer.jsp" />

</body>
</html>

