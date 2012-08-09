<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.home"/></title>
    <script type="text/javascript" src="${resources}/script/home.js"></script>
</head>

<body>

<div>
    <p><spring:message code="label.change-language"/></p>
    <p><a href="?lang=en">en</a> | <a href="?lang=es_ES">es</a></p>
</div>

<div>
    <p><spring message code="label.search"/></p>
    <form method="get" action="${ctx}/findRestaurant.html">
        <input type="text" name="loc" id="loc"/> <input type="submit" value="<spring:message code="label.search"/>"/>
    </form>
</div>

</body>
</html>
