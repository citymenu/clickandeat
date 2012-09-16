<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/restaurant.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css"/>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
    <title>${restaurant.name}</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <div class="content-left">
            <div class="restaurant-details-wrapper">
                <table width="680">
                    <tr valign="middle">
                        <td width="400">
                            <img src="${resources}/images/example.gif" width="90" height="70"/>
                            <h2><util:escape value="${restaurant.name}"/></h2>
                            <c:if test="${restaurant.description != null}">
                            <div class="restaurant-description"><util:escape value="${restaurant.description}" escapeNewLines="true"/></div>
                            </c:if>
                            <div class="restaurant-details">
                                <util:escape value="${restaurant.address.summary}"/><br>${restaurant.contactTelephone}
                            </div>
                            <c:if test="${restaurant.deliveryOptions.deliveryOptionsSummary != null}">
                            <div class="restaurant-details">
                                <util:escape value="${restaurant.deliveryOptions.deliveryOptionsSummary}" escapeComments="true" escapeNewLines="true"/>
                            </div>
                            </c:if>
                            <c:if test="${restaurant.deliveryOptions.deliveryTimeMinutes != null}">
                            <div class="restaurant-details">
                                <b>Order delivery time: ${restaurant.deliveryOptions.formattedDeliveryTimeMinutes} minutes</b>
                            </div>
                            </c:if>
                        </td>
                        <td width="280">** Will include details of any discounts, today's opening and closing times in this section, etc. **</td>
                    </tr>
                </table>
            </div>
            <div class="menu-wrapper">
                <c:if test="${restaurant.specialOfferCount > 0}">
                <div class="menu-category-wrapper">
                    <h2><spring:message code="label.special-offers"/></h2>
                    <div class="menu-category-summary"></div>
                    <c:forEach var="specialOffer" items="${restaurant.specialOffers}">
                    <div class="menu-item-wrapper">
                        <table width="680">
                            <tr valign="top">
                                <td width="510">
                                    <h3 class="menu-item-title"><util:escape value="${specialOffer.title}"/></h3>
                                </td>
                                <td width="170" align="right">
                                    <span class="menu-item-cost"><spring:message code="label.currency"/>${specialOffer.formattedCost}</span>
                                    <select:selectbox id="select_${specialOffer.specialOfferId}"/>
                                    <span class="menu-item-action">
                                        <a onclick="checkCanAddSpecialOfferToOrder('${restaurant.restaurantId}','${specialOffer.specialOfferId}',${specialOffer.specialOfferItemsArray})" class="menuitem-button add-button unselectable">A&ntilde;adir</a>
                                    </span>
                                </td>
                            </tr>
                            <c:if test="${specialOffer.description != null }">
                            <tr valign="top">
                                <td width="510">
                                    <div class="menu-item-description"><util:escape value="${specialOffer.description}" escapeNewLines="true"/></div>
                                </td>
                                <td width="170"></td>
                            </tr>
                            </c:if>
                        </table>
                        </div>
                    </c:forEach>
                </div>
                </c:if>
                <c:forEach var="menuCategory" items="${restaurant.menu.menuCategories}">
                <div class="menu-category-wrapper">
                    <h2><span class="${menuCategory.iconClass}"><util:escape value="${menuCategory.name}"/></span></h2>
                    <div class="menu-category-summary"><util:escape value="${menuCategory.summary}" escapeNewLines="true"/></div>
                    <c:choose>
                        <c:when test="${menuCategory.type == 'STANDARD'}">
                            <c:forEach var="menuItem" items="${menuCategory.menuItems}">
                            <div class="menu-item-wrapper">
                                <table width="680">
                                    <c:choose>
                                        <c:when test="${menuItem.type == 'STANDARD'}">
                                        <tr valign="top">
                                            <td width="510">
                                                <h3 class="menu-item-title"><span class="${menuItem.iconClass}"><util:escape value="${menuItem.title}"/></span> <span class="menu-item-subtitle"><util:escape value="${menuItem.subtitle}"/></span></h3>
                                            </td>
                                            <td width="170" align="right">
                                                <span class="menu-item-cost"><spring:message code="label.currency"/>${menuItem.formattedCost}</span>
                                                <select:selectbox id="select_${menuItem.itemId}"/>
                                                <span class="menu-item-action">
                                                    <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}',null,null,${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItem.nullSafeAdditionalItemCost})" class="menuitem-button add-button unselectable">A&ntilde;adir</a>
                                                </span>
                                            </td>
                                        </tr>
                                        <c:if test="${menuItem.description != null }">
                                        <tr valign="top">
                                            <td width="510">
                                                <div class="menu-item-description"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                            </td>
                                            <td width="170"></td>
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
                                                <td width="280" rowspan="${menuItem.menuItemSubTypeCount}">
                                                    <h3 class="menu-item-title"><span class="${menuItem.iconClass}"><util:escape value="${menuItem.title}"/></span> <span class="menu-item-subtitle"><util:escape value="${menuItem.subtitle}"/></span></h3>
                                                </td>
                                                </c:if>
                                                <td width="230" class="${style}">
                                                    <h3 class="menu-item-title"><util:escape value="${menuItemSubType.type}" escapeNewLines="true"/></h3>
                                                </td>
                                                <td width="170" align="right">
                                                    <span class="menu-item-cost"><spring:message code="label.currency"/>${menuItemSubType.formattedCost}</span>
                                                    <select:selectbox id="select_${menuItem.itemId}_${menuItemSubType.escapedType}"/>
                                                    <span class="menu-item-action">
                                                        <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}',null,'<util:escape value="${menuItemSubType.type}" escapeComments="true"/>',${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItem.nullSafeAdditionalItemCost})" class="menuitem-button add-button unselectable">A&ntilde;adir</a>
                                                    </span>
                                                </td>
                                            </tr>
                                            </c:forEach>
                                            <c:if test="${menuItem.description != null }">
                                            <tr valign="top">
                                                <td width="510" colspan="2">
                                                    <div class="menu-item-description"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                                </td>
                                                <td width="170"></td>
                                            </tr>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </table>
                            </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                        <c:set var="colwidth" value="${515 / menuCategory.itemTypeCount}"/>
                        <div class="menu-item-wrapper">
                            <table width="680">
                                <tr valign="top">
                                    <td width="175"></td>
                                    <c:forEach var="itemType" items="${menuCategory.itemTypes}">
                                    <td align="center" width="${colwidth}"><h3 class="menu-item-title"><util:escape value="${itemType}"/></h3></td>
                                    </c:forEach>
                                </tr>
                            </table>
                        </div>
                        <c:forEach var="menuItem" items="${menuCategory.menuItems}">
                        <div class="menu-item-wrapper">
                            <table width="680">
                                <tr valign="top">
                                    <td width="175">
                                        <h3 class="menu-item-title"><span class="${menuItem.iconClass}"><util:escape value="${menuItem.title}"/></span> <span class="menu-item-subtitle"><util:escape value="${menuItem.subtitle}"/></span></h3>
                                    </td>
                                    <c:forEach var="menuItemTypeCost" items="${menuItem.menuItemTypeCosts}">
                                    <td align="right" width="${colwidth}">
                                        <c:if test="${menuItemTypeCost.cost != null}">
                                        <span class="menu-item-cost"><spring:message code="label.currency"/>${menuItemTypeCost.formattedCost}</span>
                                        <select:selectbox id="select_${menuItem.itemId}_${menuItemTypeCost.escapedType}"/>
                                        <span class="menu-item-action">
                                            <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}','<util:escape value="${menuItemTypeCost.type}" escapeComments="true"/>',null,${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItemTypeCost.nullSafeAdditionalItemCost})" class="menuitem-button add-button unselectable">A&ntilde;adir</a>
                                        </span>
                                        </c:if>
                                    </td>
                                    </c:forEach>
                                </tr>
                                <c:if test="${menuItem.description != null }">
                                <tr valign="top">
                                    <td width="175">
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
                        </table>
                        </c:otherwise>
                    </c:choose>
                </div>
                </c:forEach>
            </div>
        </div>
        <div class="content-right">
            <%@ include file="/WEB-INF/jsp/order.jsp" %>
            <br/>
            <br/>
            <br/>
            <div style="text-align:center; padding: 10px;">** Could include a table of all of the opening times for the restaurant here **</div>
            <br/>
            <br/>
            <div style="text-align:center;">** Could include google maps link to restaurant location like below here: **</div>
            <div style="text-align:center; padding: 10px;">
                <img src="http://maps.googleapis.com/maps/api/staticmap?center=${restaurant.address.postCode}&zoom=15&size=200x135&maptype=roadmap&markers=color:blue%7Clabel:S%7C${restaurant.address.postCode}&sensor=false"/>
            </div>

        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>

</body>
</html>
