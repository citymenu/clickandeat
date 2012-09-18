<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <script type="text/javascript" src="${resources}/script/home.js"></script>
    <title><spring:message code="label.home"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <div class="header-tagline">
            <h1>Simply the <span class="classy">easiest</span> way to order takeaway online.</h1>
            <input class="postCodeInput" type="text" name="loc" id="loc"/>
            <input class="findButton" type="button" id="searchbutton" value="<spring:message code="label.search"/>"/>
        </div>
        <div style="text-align: center; margin: 40px 0 30px 0;">
            <div>Thought we could use something like the below to explain how the process of ordering works. See <a href="http://developers.slidedeck.com">SlideDeck</a> for a working demo.</div>
            <br/>
            <div><img src="${resources}/images/slidedeck.jpg"/></div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>
