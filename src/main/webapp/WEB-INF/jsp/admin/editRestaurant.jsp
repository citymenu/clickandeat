<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
	<script type="text/javascript" src="${resources}/admin/app/EditRestaurant.js"></script>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBV3hoZjKpsmV0HYAICzvct4rIwSIG2I-8&language=<locale:language/>&sensor=false"></script>
    <script type="text/javascript" src="${resources}/script/googlemap.js"></script>
    <title>LlamaryComer - Restaurant</title>

    <script type="text/javascript">
        var restaurantObj;
        var restaurantId = '${restaurantId}';
        var cuisines = [${cuisinesArray}];
    </script>

    <style type="text/css">
        #map_canvas {
            width: 450px;
            height: 400px;
        }
    </style>

</head>

<body>

<div id="north">
    <%@ include file="/WEB-INF/jsp/admin/header.jsp" %>
</div>

<div id="main-content"></div>

</body>