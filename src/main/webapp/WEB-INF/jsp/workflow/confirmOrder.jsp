<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/workflow.css"/>
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
                        <h2><message:message key="workflow.order-update-status"/></h2>
                        <div class="order-confirmation-header-wrapper">
                            <div><util:escape value="${message}"/></div>
                        </div>
                    </td>
                </td>
                <td width="360">
                    <div class="butler">
                    Image of the butler here
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>

</body>
