<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/workflow.css" charset="utf-8">
    <title>LlamaryComer | <message:message key="workflow.order-confirmation"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/header.jsp" %>

<div id="content">
    <div class="content-wrapper">
        <table width="1020">
            <tr valign="top">
                <td width="660">
                    <div class="order-confirmation-wrapper">
                        <h2><message:message key="workflow.order-update-status" format="${order.orderId}"/></h2>
                        <div class="order-confirmation-header-wrapper">
                            <div><util:escape value="${message}"/></div>
                        </div>
                    </td>
                </td>
                <td width="360">
                    <div class="butler"></div>
                </td>
            </tr>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/${systemLocale}/footer.jsp" />

</body>
