<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div id="footer">
    <div class="footer-wrapper">
        <table width="980">
            <tr valign="top">
                <td width="490"></td>
                <td width="245"></td>
                <td width="245">
                    <h3>Serving locations</h3>
                    <div class="location-list">
                    <div class="location-list">
                    <c:forEach var="location" items="${locations}">
                    <p><a href="${ctx}/app/find takeaway in ${location}/browse/loc/${location}">${location}</a></p>
                    </c:forEach>
                    </div>
                    <h3>Cuisines</h3>
                    <c:forEach var="cuisine" items="${cuisines}">
                    <p><a href="${ctx}/app/find ${cuisine} takeaway/browse/cuisine/${cuisine}">${cuisine}</a></p>
                    </c:forEach>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

