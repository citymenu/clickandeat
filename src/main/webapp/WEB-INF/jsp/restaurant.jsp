<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title>${restaurant.name}</title>
    <script type="text/javascript" src="${resources}/script/tools.js"></script>
    <script type="text/javascript" src="${resources}/script/restaurant.js"></script>
    <script type="text/javascript" src="${resources}/script/orders.js"></script>
</head>

<body>


<div id="maincontent">
    <%@ include file="/WEB-INF/jsp/workflow.jsp" %>
    <div id="contentbody">

        <div id="restaurant">${restaurant.name}</div>
        <div>${restaurant.description}</div>

        <div class="menu">
            <c:forEach var="menuCategory" items="${restaurant.menu.menuCategories}">
                <div class="menucategory">
                    <div class="menucategoryheader">
                        <div class="menucategoryname">${menuCategory.name}</div>
                        <div class="menucategorysummary">${menuCategory.summary}</div>
                    </div>
                    <div class="menuitems">
                        <table width="100%" cellpadding="0" cellspacing="0" border="0" class="menuItemTable">
                            <c:forEach var="menuItem" items="${menuCategory.menuItems}">
                                <tr valign="top">
                                    <td width="80%">
                                        <div class="menuItemDetails">
                                            <div class="menuItemNumber">${menuItem.number}</div>
                                            <div class="menuItemTitle">${menuItem.title} <div class="menuItemSubtitle">${menuItem.subtitle}</div></div>
                                            <div class="menuItemDescription">${menuItem.description}</div>
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
                                                <a onclick="addMultipleToOrder('${restaurant.restaurantId}',${menuItem.number},'${menuItem.itemId}','${fn:replace(menuItem.title,"'","###")}',${menuItem.cost})">
                                                    <img title="<spring:message code="label.add-to-order"/>" src="${resources}/images/icons-shadowless/plus-button.png"/>
                                                </a>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<div id="rightbar">
    <%@ include file="/WEB-INF/jsp/order.jsp" %>
</div>

</body>
</html>
