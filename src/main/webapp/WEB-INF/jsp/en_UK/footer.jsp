<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div id="footer">
    <div class="footer-wrapper">
        <table width="980">
            <tr valign="top">
                <td width="455"></td>
                <td width="175">
                    <h3 class="footer">Contact</h3>
                </td>
                <td width="175">
                    <h3 class="footer">Company</h3>
                </td>
                <td width="175">
                    <h3 class="footer">Now Serving</h3>
                    <div class="location-list">
                    <c:forEach var="location" items="${locations}">
                    <p><a class="location" href="${ctx}/app/find-takeaway-food-in-${location}/loc/${location}">${location} Takeaway</a></p>
                    </c:forEach>
                    <c:forEach var="cuisine" items="${cuisines}">
                    <p><a class="location" href="${ctx}/app/find-${cuisine}-takeaway-food/csn/${cuisine}">${cuisine} Takeaway</a></p>
                    </c:forEach>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

