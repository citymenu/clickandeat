<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
	<script type="text/javascript" src="${ctx}/resources/admin/app/EditRestaurant.js"></script>
    <title>ClickAndEat - Restaurants</title>

    <pre>
    <script type="text/javascript">
        var restaurantObj = JSON.parse('${json}');
        var cuisines = [${cuisinesArray}];
    </script>
    </pre>

</head>

<body>
<div id="main-content"></div>
</body>