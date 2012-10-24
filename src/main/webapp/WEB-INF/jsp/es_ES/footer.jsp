<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div id="footer">
    <div class="footer-wrapper">
        <table width="1020">
            <tr valign="top">
                <td width="465">
                    <h3 class="footer">Simply the easiest way to order takeaway food onlin</h3>
                </td>
                <td width="185">
                    <h3 class="footer">Contact</h3>
                </td>
                <td width="185">
                    <h3 class="footer">Company</h3>
                </td>
                <td width="185">
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

