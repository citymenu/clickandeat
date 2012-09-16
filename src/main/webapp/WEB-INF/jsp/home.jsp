<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.home"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <div class="header-tagline">
            <h1>Simply the <span class="classy">easiest</span> way to order takeaway online.</h1>
            <form method="get" action="${ctx}/findRestaurant.html">
                <input class="postCodeInput" type="text" name="loc" id="loc"/>
                <input class="findButton" type="submit" value="<spring:message code="label.search"/>"/>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>
