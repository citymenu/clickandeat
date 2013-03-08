<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/workflow.css" charset="utf-8"/>
    <title><message:message key="title.companyname"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <td width="660">
                    <div class="order-confirmation-wrapper">
                        <h2>${message}</h2>

                    </td>
                </td>
                <td width="360">
                    <c:choose>
                        <c:when test="${restaurant.contentApproved}">
                            <div class="butler-smile"></div>
                        </c:when>
                        <c:otherwise>
                            <div class="butler"></div>
                        </c:otherwise>
                    </c:choose>

                </td>
            </tr>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/${systemLocale}/footer.jsp" />

</body>
