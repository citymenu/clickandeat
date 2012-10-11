<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&language=<locale:language/>&sensor=false"></script>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/restaurant.css"/>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <script type="text/javascript" src="${resources}/script/restaurant.js"></script>
    <script type="text/javascript" src="${resources}/script/googlemap.js"></script>
    <title>${restaurant.name}</title>
    <script type="text/javascript">var restaurantId='${restaurant.restaurantId}';</script>
    <script type="text/javascript">var restaurantName='<util:escape value="${restaurant.name}" escapeComments="true"/>';</script>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<!-- Menu item index -->
<div id="content">
    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <td width="1020" colspan="3">
                    <div class="restaurant-details-wrapper">
                        <table width="1000">
                            <tr valign="top">
                                <td width="460">
                                    <table width="460">
                                        <tr valign="bottom">
                                            <td nowrap>
                                                <img src="${resources}/images/restaurant/${restaurant.imageName}"/>
                                            </td>
                                            <td width="100%">
                                                <div class="restaurant-title">
                                                    <h2><util:escape value="${restaurant.name}"/></h2>
                                                    <c:if test="${restaurant.description != null}">
                                                    <div class="restaurant-description"><util:escape value="${restaurant.description}" escapeNewLines="true"/></div>
                                                    </c:if>
                                                    <div class="cuisine-summary">${restaurant.cuisineSummary}</div>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                    <div class="restaurant-summary">
                                        <div class="restaurant-details">
                                            <util:escape value="${restaurant.address.summary}"/>
                                            <c:if test="${restaurant.contactTelephone != ''}">
                                            <br>${restaurant.contactTelephone}
                                            </c:if>
                                        </div>
                                        <div class="restaurant-delivery-details">
                                            <c:if test="${restaurant.deliveryTimeMinutes != 0}">
                                            <message:message key="order.delivery-time"/> ${restaurant.deliveryTimeMinutes} <message:message key="time.minutes"/>
                                            </c:if>
                                            <c:if test="${restaurant.collectionTimeMinutes != 0}">
                                            <br><message:message key="order.collection-time"/> ${restaurant.collectionTimeMinutes} <message:message key="time.minutes"/>
                                            </c:if>
                                        </div>

                                    </div>
                                </td>
                                <td width="340">
                                    <c:if test="${restaurant.hasDiscounts == true}">
                                        <div class="restaurant-discount-details">
                                            <div class="scissors"></div>
                                            <c:forEach var="discount" items="${restaurant.discounts}">
                                                <div class="discount-details"><util:escape value="${discount.title}"/></div>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                    <div class="restuarant-summary-wrapper">
                                        <div class="restaurant-detail-wrapper">
                                            <div class="restaurant-opening-details">
                                                <div class="opening-details">
                                                    <message:message key="search.open-today"/>: ${restaurant.todaysOpeningTimes}&nbsp;&nbsp;<a class="restaurant-text" onclick="showAllOpeningTimes()"><message:message key="restaurant.opening-show-all"/></a>
                                                </div>
                                                <div class="delivery-details"><util:escape value="${restaurant.deliveryOptions.deliveryOptionsSummary}" escapeNewLines="true" escapeComments="true"/></div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td width="200">
                                    <div class="restaurant-location">
                                        <div class="restaurant-map">
                                            <img width="200" height="120" src="http://maps.googleapis.com/maps/api/staticmap?center=${restaurant.coordinates}&zoom=15&size=200x120&scale=2&maptype=roadmap&markers=color:blue%7Clabel:B%7C${restaurant.coordinates}&sensor=false"/>
                                        </div>
                                        <c:choose>
                                            <c:when test="${search != null && search.coordinates != null}">
                                                <div class="restaurant-details"><a class="restaurant-text" onclick="showDirections(${restaurant.coordinates},${search.coordinates},'<util:escape value="${restaurant.name}" escapeComments="true"/>')">Get directions</a> from your location</div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="restaurant-details"><a class="restaurant-text" onclick="showDirections(${restaurant.coordinates},null,null,'<util:escape value="${restaurant.name}" escapeComments="true"/>')">Show location</a></div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
            <tr valign="top">
                <!-- Menu category launchpad -->
                <td width="180">
                    <div class="menu-left">
                        <div id="menu-launch-wrapper">
                            <h2><message:message key="restaurant.menu"/>:</h2>
                            <div class="menu-launch-description"><message:message key="restaurant.menu-launch-description"/></div>
                            <div class="menu-launch-content-wrapper">
                                <c:forEach var="menuCategory" items="${restaurant.menu.menuCategories}">
                                <div class="menu-launch-entry">
                                    <a onclick="jump('${menuCategory.categoryId}')"><util:escape value="${menuCategory.name}"/></a>
                                </div>
                                </c:forEach>
                                <c:if test="${restaurant.specialOfferCount > 0}">
                                <div class="menu-launch-entry">
                                    <a onclick="jump('special_offers')"><message:message key="order.special-offers"/></a>
                                </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </td>

                <!-- Menu -->
                <td width="580">
                    <div class="menu-center">
                        <c:forEach var="menuCategory" items="${restaurant.menu.menuCategories}">
                        <div class="menu-category-wrapper" id="${menuCategory.categoryId}">
                            <h2><span class="${menuCategory.iconClass}"><util:escape value="${menuCategory.name}"/></span></h2>
                            <div class="menu-category-summary"><util:escape value="${menuCategory.summary}" escapeNewLines="true"/></div>
                            <c:choose>
                                <c:when test="${menuCategory.type == 'STANDARD'}">
                                    <c:forEach var="menuItem" items="${menuCategory.menuItems}" varStatus="index">
                                    <div class="menu-item-wrapper">
                                        <table width="520">
                                            <c:choose>
                                                <c:when test="${menuItem.type == 'STANDARD'}">
                                                <tr valign="top">
                                                    <td width="420">
                                                        <h3 class="menu-item-title"><span class="${menuItem.iconClass}"><util:escape value="${menuItem.title}"/></span> <span class="menu-item-subtitle"><util:escape value="${menuItem.subtitle}"/></span></h3>
                                                    </td>
                                                    <td width="100" align="right">
                                                        <span class="menu-item-cost"><message:message key="config.currency" escape="false"/>${menuItem.formattedCost}</span>
                                                        <span class="menu-item-action">
                                                            <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}',null,null,${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItem.nullSafeAdditionalItemCost}, ${menuItem.cost})" class="menuitem-button add-button unselectable"></a>
                                                        </span>
                                                    </td>
                                                </tr>
                                                <c:if test="${menuItem.description != null }">
                                                <tr valign="top">
                                                    <td width="420">
                                                        <div class="menu-item-description"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                                    </td>
                                                    <td width="100"></td>
                                                </tr>
                                                </c:if>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="menuItemSubType" items="${menuItem.menuItemSubTypes}" varStatus="status">
                                                    <tr valign="top">
                                                        <c:set var="style" value=""/>
                                                        <c:if test="${status.count < menuItem.menuItemSubTypeCount}">
                                                            <c:set var="style" value="spacer"/>
                                                        </c:if>
                                                        <c:if test="${status.count == 1}">
                                                        <td width="300" rowspan="${menuItem.menuItemSubTypeCount}">
                                                            <h3 class="menu-item-title"><span class="${menuItem.iconClass}"><util:escape value="${menuItem.title}"/></span> <span class="menu-item-subtitle"><util:escape value="${menuItem.subtitle}"/></span></h3>
                                                            <c:if test="${menuItem.description != null }">
                                                                <div class="menu-item-description"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                                            </c:if>
                                                        </td>
                                                        </c:if>
                                                        <td width="120" class="${style}">
                                                            <h3 class="menu-item-title"><util:escape value="${menuItemSubType.type}" escapeNewLines="true"/></h3>
                                                        </td>
                                                        <td width="100" align="right">
                                                            <span class="menu-item-cost"><message:message key="config.currency" escape="false"/>${menuItemSubType.formattedCost}</span>
                                                            <span class="menu-item-action">
                                                                <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}',null,'<util:escape value="${menuItemSubType.type}" escapeComments="true"/>',${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItem.nullSafeAdditionalItemCost},${menuItemSubType.cost})" class="menuitem-button add-button unselectable"></a>
                                                            </span>
                                                        </td>
                                                    </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </table>
                                    </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                <c:set var="colwidth" value="${340 / menuCategory.itemTypeCount}"/>
                                <div class="menu-item-title-wrapper">
                                    <table width="520">
                                        <tr valign="top">
                                            <td width="180"></td>
                                            <c:forEach var="itemType" items="${menuCategory.itemTypes}">
                                            <td align="center" width="${colwidth}"><h3 class="menu-item-title"><util:escape value="${itemType}"/></h3></td>
                                            </c:forEach>
                                        </tr>
                                    </table>
                                </div>
                                <c:forEach var="menuItem" items="${menuCategory.menuItems}">
                                <div class="menu-item-wrapper">
                                    <table width="520">
                                        <tr valign="top">
                                            <td width="180">
                                                <h3 class="menu-item-title"><span class="${menuItem.iconClass}"><util:escape value="${menuItem.title}"/></span> <span class="menu-item-subtitle"><util:escape value="${menuItem.subtitle}"/></span></h3>
                                            </td>
                                            <c:forEach var="menuItemTypeCost" items="${menuItem.menuItemTypeCosts}">
                                            <td align="right" width="${colwidth}">
                                                <c:if test="${menuItemTypeCost.cost != null}">
                                                <span class="menu-item-cost"><message:message key="config.currency" escape="false"/>${menuItemTypeCost.formattedCost}</span>
                                                <span class="menu-item-action">
                                                    <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}','<util:escape value="${menuItemTypeCost.type}" escapeComments="true"/>',null,${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItemTypeCost.nullSafeAdditionalItemCost},${menuItemTypeCost.cost})" class="menuitem-button add-button unselectable"></a>
                                                </span>
                                                </c:if>
                                            </td>
                                            </c:forEach>
                                        </tr>
                                        <c:if test="${menuItem.description != null }">
                                        <tr valign="top">
                                            <td width="180">
                                                <div class="menu-item-description"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                            </td>
                                            <c:forEach var="menuItemTypeCost" items="${menuItem.menuItemTypeCosts}">
                                            <td width="${colwidth}"></td>
                                            </c:forEach>
                                        </tr>
                                        </c:if>
                                    </table>
                                </div>
                                </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        </c:forEach>
                        <c:if test="${restaurant.specialOfferCount > 0}">
                        <div class="menu-category-wrapper" id="special_offers">
                            <h2><message:message key="order.special-offers"/></h2>
                            <div class="menu-category-summary"></div>
                            <c:forEach var="specialOffer" items="${restaurant.specialOffers}">
                            <div class="menu-item-wrapper">
                                <table width="520">
                                    <tr valign="top">
                                        <td width="400">
                                            <h3 class="menu-item-title"><util:escape value="${specialOffer.title}"/></h3>
                                        </td>
                                        <td width="120" align="right">
                                            <span class="menu-item-cost"><message:message key="config.currency" escape="false"/>${specialOffer.formattedCost}</span>
                                            <span class="menu-item-action">
                                                <a onclick="checkCanAddSpecialOfferToOrder('${restaurant.restaurantId}','${specialOffer.specialOfferId}',${specialOffer.specialOfferItemsArray},${specialOffer.cost})" class="menuitem-button add-button unselectable">&nbsp;</a>
                                            </span>
                                        </td>
                                    </tr>
                                    <c:if test="${specialOffer.description != null }">
                                    <tr valign="top">
                                        <td width="400">
                                            <div class="menu-item-description"><util:escape value="${specialOffer.description}" escapeNewLines="true"/></div>
                                        </td>
                                        <td width="120"></td>
                                    </tr>
                                    </c:if>
                                </table>
                                </div>
                            </c:forEach>
                        </div>
                        </c:if>
                    </div>
                </td>

                <!-- Order panel -->
                <td width="260">
                    <div class="menu-right">
                        <%@ include file="/WEB-INF/jsp/order.jsp" %>
                    </div>
                </td>

            </tr>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>

</body>
</html>
