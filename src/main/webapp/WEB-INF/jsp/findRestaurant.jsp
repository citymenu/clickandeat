<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>


<head>
    <title><spring:message code="label.search"/></title>
    <script type="text/javascript" src="${ctx}/resources/script/findRestaurant.js"></script>
    <script type="text/javascript" src="${ctx}/resources/script/orders.js"></script>
</head>

<body>

<div id="maincontent">
    <%@ include file="/WEB-INF/jsp/workflow.jsp" %>
    <div id="contentbody">

        <div id="searchresultsleft">
            <div id="search" class="boxcontainer">
                <form method="GET" action="${ctx}/search.html">
                    <h3><spring:message code="label.search"/></h3>
                    <div>Location:</div>
                    <div><input name="loc" type="text" class="search" value="${search.location}"/></div>
                    <input type="submit" value="<spring:message code="label.search"/>"/>
                    <div>Cuisines:</div>
                    <c:forEach var="cuisine" items="${cuisines}">
                        <c:set var="checked" value=""/>
                        <c:forEach var="c" items="${search.cuisines}">
                            <c:if test="${c eq cuisine}">
                                <c:set var="checked" value="checked='true'" />
                            </c:if>
                        </c:forEach>
                        <div><input type="checkbox" name="c" value="${cuisine}" ${checked}/> ${cuisine}</div>
                    </c:forEach>
                </form>
            </div>
        </div>

        <div id="searchresultsright">
            <div id="results">
                <div id="searchheader" class="boxcontainer">
                    <h3>Showing ${count} restaurants serving location ${search.location}</h3>
                </div>
                <c:forEach var="restaurant" items="${results}">
                    <div class="boxcontainer searchresult">
                        <a href="${ctx}/restaurant.html?restaurantId=${restaurant.restaurantId}">${restaurant.name}</a>
                        <div class="result-description">${restaurant.description}</div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

</div>

<div id="rightbar">
    <%@ include file="/WEB-INF/jsp/order.jsp" %>
</div>

</body>
</html>
