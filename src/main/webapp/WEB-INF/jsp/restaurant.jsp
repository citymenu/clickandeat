<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title>${restaurant.name}</title>
    <script type="text/javascript" src="${ctx}/resources/script/orders.js"></script>
</head>

<body>

<h1>${restaurant.name}</h1>
<div>${restaurant.description}</div>

<h1>Menu</h1>
<div class="menu">
    <c:forEach var="menuCategory" items="${restaurant.menu.menuCategories}">
        <div class="menucategory">
            <h2>${menuCategory.name}</h2>
            <div class="menucategorysummary">${menuCategory.summary}</div>
            <div class="menuitems">
                <c:forEach var="menuItem" items="${menuCategory.menuItems}">
                    <div class="menuItem">
                        <div>${menuItem.number}) ${menuItem.title} ${menuItem.subtitle} <span><a href="#" onclick="addToOrder('${restaurant.restaurantId}','${menuItem.itemId}','${menuItem.title}',${menuItem.cost})">Add</a></span></div>
                        <div>${menuItem.description}</div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:forEach>
</div>

</body>
</html>
