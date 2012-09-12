<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <!-- css -->
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/main.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/header.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/content.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/footer.css"/>

    <!-- Typekit -->
    <script type="text/javascript" src="//use.typekit.net/iwp4tpg.js"></script>
    <script type="text/javascript">try{Typekit.load();}catch(e){}</script>
    <title><spring:message code="label.home"/></title>
</head>

<body>

<div id="header">
    <div class="header-wrapper">
        <div class="header-banner">
            <div class="header-company unselectable">llamar y comer</div>
        </div>
        <div class="navigation-wrapper">
        </div>
    </div>
</div>

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
