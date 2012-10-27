<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
	<script type="text/javascript" src="${resources}/admin/app/EditRestaurant.js"></script>
    <title>ClickAndEat - Restaurants</title>

    <script type="text/javascript">
        var restaurantObj;
        var restaurantId = '${restaurantId}';
        var cuisines = [${cuisinesArray}];
    </script>

</head>

<body>

<div id="north">
    <%@ include file="/WEB-INF/jsp/admin/header.jsp" %>
</div>

<div id="main-content"></div>

</body>