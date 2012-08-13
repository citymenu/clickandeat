<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <title><spring:message code="label.home"/></title>
    <script type="text/javascript" src="${resources}/script/home.js"></script>
</head>

<body>

<div>
    <p><spring message code="label.search"/></p>
    <form method="get" action="${ctx}/findRestaurant.html">
        <div class="homesearch">
            <table width="100%" cellpadding="0" cellspacing="0">
                <tr valign="middle">
                    <td align="right">
                        <input class="postCodeInput" type="text" name="loc" id="loc"/>
                    </td>
                    <td align="left">
                        <input class="findButton" type="submit" value="<spring:message code="label.search"/>"/>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>

</body>
</html>
