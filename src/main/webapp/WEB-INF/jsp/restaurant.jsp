<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title>${restaurant.name}</title>
    <script type="text/javascript" src="${resources}/script/tools.js"></script>
    <script type="text/javascript" src="${resources}/script/restaurant.js"></script>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
</head>

<body>

<script type="text/javascript">
    var searchLocation="${search.location}";
</script>

<div id="maincontent">
    <%@ include file="/WEB-INF/jsp/workflow.jsp" %>
    <div id="contentbody">

        <div id="restaurant"><util:escape value="${restaurant.name}"/></div>
        <div><util:escape value="${restaurant.description}" escapeNewLines="true"/></div>

        <div class="menu">
            <c:forEach var="menuCategory" items="${restaurant.menu.menuCategories}">
                <div class="menucategory">
                    <div class="menucategoryheader">
                        <div class="menucategoryname"><util:escape value="${menuCategory.name}"/></div>
                        <div class="menucategorysummary"><util:escape value="${menuCategory.summary}" escapeNewLines="true"/></div>
                    </div>
                    <div class="menuitems">
                        <table width="100%" cellpadding="0" cellspacing="0" border="0" class="menuItemTable">
                            <c:choose>
                                <c:when test="${menuCategory.type == 'STANDARD'}">
                                    <c:forEach var="menuItem" items="${menuCategory.menuItems}">
                                        <c:choose>
                                            <c:when test="${menuItem.type == 'STANDARD'}">
                                                <tr valign="top">
                                                    <td width="80%" colspan="2">
                                                        <div class="menuItemDetails">
                                                            <div class="menuItemNumber">${menuItem.number}</div>
                                                            <div class="menuItemTitle"><util:escape value="${menuItem.title}" escapeNewLines="true"/> <div class="menuItemSubtitle"><util:escape value="${menuItem.subtitle}" escapeNewLines="true"/></div></div>
                                                            <div class="menuItemDescription"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                                        </div>
                                                    </td>
                                                    <td width="20%" align="right">
                                                        <div class="menuItemActions">
                                                            <div class="menuItemCost"><spring:message code="label.currency"/>${menuItem.formattedCost}</div>
                                                            <div class="menuItemAction">
                                                                <select class="menuItemQuantity" id="select_${menuItem.itemId}">
                                                                    <option value="1">1</option>
                                                                    <option value="2">2</option>
                                                                    <option value="3">3</option>
                                                                    <option value="4">4</option>
                                                                    <option value="5">5</option>
                                                                </select>
                                                                <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}',null,null,${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItem.nullSafeAdditionalItemCost})">
                                                                    <img title="<spring:message code="label.add-to-order"/>" src="${resources}/images/icons-shadowless/plus-button.png"/>
                                                                </a>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="menuItemSubType" items="${menuItem.menuItemSubTypes}" varStatus="status">
                                                    <tr valign="top">
                                                        <td width="65%">
                                                            <c:if test="${status.count == 1}">
                                                                <div class="menuItemDetails">
                                                                    <div class="menuItemNumber">${menuItem.number}</div>
                                                                    <div class="menuItemTitle"><util:escape value="${menuItem.title}" escapeNewLines="true"/> <div class="menuItemSubtitle"><util:escape value="${menuItem.subtitle}" escapeNewLines="true"/></div></div>
                                                                    <div class="menuItemDescription"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                                                </div>
                                                            </c:if>
                                                        </td>
                                                        <td width="15%">
                                                            <div class="menuItemDetails">
                                                                <div class="menuItemTitle">
                                                                    <util:escape value="${menuItemSubType.type}" escapeNewLines="true"/>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td width="20%" align="right">
                                                            <div class="menuItemActions">
                                                                <div class="menuItemCost"><spring:message code="label.currency"/>${menuItemSubType.formattedCost}</div>
                                                                <div class="menuItemAction">
                                                                    <select class="menuItemQuantity" id="select_${menuItem.itemId}_<util:escape value="${menuItemSubType.type}" escapeComments="true"/>">
                                                                        <option value="1">1</option>
                                                                        <option value="2">2</option>
                                                                        <option value="3">3</option>
                                                                        <option value="4">4</option>
                                                                        <option value="5">5</option>
                                                                    </select>
                                                                    <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}',null,'<util:escape value="${menuItemSubType.type}" escapeComments="true"/>',${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItem.nullSafeAdditionalItemCost})">
                                                                        <img title="<spring:message code="label.add-to-order"/>" src="${resources}/images/icons-shadowless/plus-button.png"/>
                                                                    </a>
                                                                </div>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <thead>
                                        <th width="25%"></th>
                                        <c:forEach var="itemType" items="${menuCategory.itemTypes}">
                                            <th align="center"><util:escape value="${itemType}"/></th>
                                        </c:forEach>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="menuItem" items="${menuCategory.menuItems}">
                                            <tr valign="top">
                                                <td width="25%">
                                                    <div class="menuItemDetails">
                                                        <div class="menuItemNumber">${menuItem.number}</div>
                                                        <div class="menuItemTitle"><util:escape value="${menuItem.title}" escapeNewLines="true"/> <div class="menuItemSubtitle"><util:escape value="${menuItem.subtitle}" escapeNewLines="true"/></div></div>
                                                        <div class="menuItemDescription"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                                    </div>
                                                </td>
                                                <c:forEach var="menuItemTypeCost" items="${menuItem.menuItemTypeCosts}">
                                                    <td align="right">
                                                        <c:if test="${menuItemTypeCost.cost != null}">
                                                            <div class="menuItemActions">
                                                                <div class="menuItemCost"><spring:message code="label.currency"/>${menuItemTypeCost.formattedCost}</div>
                                                                <div class="menuItemAction">
                                                                    <select class="menuItemQuantity" id="select_${menuItem.itemId}_<util:escape value="${menuItemTypeCost.type}" escapeComments="true"/>">
                                                                        <option value="1">1</option>
                                                                        <option value="2">2</option>
                                                                        <option value="3">3</option>
                                                                        <option value="4">4</option>
                                                                        <option value="5">5</option>
                                                                    </select>

                                                                    <a onclick="addMultipleToOrder('${restaurant.restaurantId}','${menuItem.itemId}','<util:escape value="${menuItemTypeCost.type}" escapeComments="true"/>',null,${menuItem.additionalItemChoiceArray},${menuItem.nullSafeChoiceLimit},${menuItemTypeCost.nullSafeAdditionalItemCost})">
                                                                        <img title="<spring:message code="label.add-to-order"/>" src="${resources}/images/icons-shadowless/plus-button.png"/>
                                                                    </a>
                                                                </div>
                                                            </div>
                                                        </c:if>
                                                    </td>
                                                </c:forEach>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </c:otherwise>
                            </c:choose>
                        </table>
                    </div>
                </div>
            </c:forEach>
        </div>

        <div>
            <input type="button" id="selectanotherbutton" value="<spring:message code="label.select-another-restaurant"/>"/>
        </div>

    </div>
</div>

<div id="rightbar">
    <%@ include file="/WEB-INF/jsp/order.jsp" %>
</div>

</body>
</html>
