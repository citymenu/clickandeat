<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <!-- css -->
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/main.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/header.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/content.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/footer.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/restaurant.css"/>

    <!-- Typekit -->
    <script type="text/javascript" src="//use.typekit.net/iwp4tpg.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>
    <title>${restaurant.name}</title>
</head>

<body>

<div id="header">
    <div class="header-wrapper">
        <div class="header-banner">
            <div class="header-company">llamar y comer</div>
        </div>
        <div class="navigation-wrapper">
        </div>
    </div>
</div>

<div id="content">
    <div class="content-wrapper">
        <div class="restaurant-details-wrapper">
            <h2>${restaurant.name}</h2>
            <div class="restaurant-details">
                ${restaurant.address.summary}<br>${restaurant.contactTelephone}
            </div>
            <div class="restaurant-details">
                <span class="restaurant-details-title">Today's opening times: </span>${restaurant.todaysOpeningTimes}</span>
            </div>
        </div>
        <div class="content-left">
            <div class="menu-wrapper">
                <c:forEach var="menuCategory" items="${restaurant.menu.menuCategories}">
                <div class="menu-category-wrapper">
                    <h2><util:escape value="${menuCategory.name}"/></h2>
                    <div class="menu-category-summary"><util:escape value="${menuCategory.summary}" escapeNewLines="true"/></div>
                    <c:forEach var="menuItem" items="${menuCategory.menuItems}">
                    <div class="menu-item-wrapper">
                        <div class="menu-item-main-wrapper">
                            <div class="menu-item-title-wrapper">
                                <h3><util:escape value="${menuItem.title}"/> <span class="menu-item-subtitle"><util:escape value="${menuItem.subtitle}"/></span></h3>
                                <c:if test="${menuItem.description != null }">
                                <div class="menu-item-description"><util:escape value="${menuItem.description}" escapeNewLines="true"/></div>
                                </c:if>
                            </div>
                            <div class="menu-item-cost-wrapper">
                                <table width="100" cellpadding="0" cellspacing="0" border="0">
                                    <tr valign="top">
                                        <td width="50" align="right"><span class="menu-item-cost"><spring:message code="label.currency"/>${menuItem.formattedCost}</span></td>
                                        <td width="50" align="right"><span class="menu-item-action"><a class="menuitem-button">Add</a></span></td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                    </c:forEach>
                </div>
                </c:forEach>
            </div>
        </div>
        <div class="content-right">
        </div>
    </div>
</div>

<div id="footer">
    <div class="footer-wrapper">
        <div class="footer-contact">
            <div class="third left">
                <h4 class="footer">General contact</h4>
                <ul>
                    <li>Phone:
                    <br>+44(208)5057191
                    </li>
                </ul>
            </div>
            <div class="third left"><h4 class="footer">Sales and customer support</h4></div>
            <div class="third left last"><h4 class="footer">Stay in touch</h4></div>
        </div>
    </div>
</div>

</body>
</html>
