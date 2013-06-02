<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!doctype html>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/orders.css" charset="utf-8">
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/payment.css" charset="utf-8">

    <script type="text/javascript" src="${resources}/script/orders.js" charset="utf-8"></script>
    <script type="text/javascript" src="${resources}/script/payment.js" charset="utf-8"></script>
    <script type="text/javascript">var error='<util:escape value="${error}" escapeNewLines="true"/>';</script>

    <title><message:message key="page-title.payment" escape="false"/></title>

    <script type="text/javascript">
    // Prevent variables from being global
    (function () {
        var div = document.createElement('div'),
            ref = document.getElementsByTagName('base')[0] ||
                  document.getElementsByTagName('script')[0];
        div.innerHTML = '&shy;<style> iframe { visibility: hidden; } </style>';
        ref.parentNode.insertBefore(div, ref);
        window.onload = function() {
            div.parentNode.removeChild(div);
        }
    })();
    </script>

</head>

<body>
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
    <div id="content">
        <div class="content-wrapper">
            <table width="1000">
                <tr valign="top">
                    <!-- Payment form -->
                    <td width="740">
                        <div class="payment-wrapper">
                            <div class="payment-title-wrapper">
                                <div class="payment-title"><h2><message:message key="payment.payment-details"/></h2></div>
                            </div>
                            <div id="paymentbody">
                                <iframe class="payment" name="paymentForm" src="${ctx}/cardProcessing.html" frameborder="0" border="0" cellspacing="0" scrolling="no" style="border-style: none;width: 100%; height: 100%;"></iframe>
                            </div>
                        </div>
                    </td>

                    <!-- Order panel -->
                    <td width="260">
                        <div class="menu-right">
                            <%@ include file="/WEB-INF/jsp/order.jsp" %>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>

</body>
</html>
