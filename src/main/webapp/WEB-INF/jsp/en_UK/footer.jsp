<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div id="footer">
    <div class="footer-wrapper">
        <table width="1020">
            <tr valign="top">
                <td width="620">
                    <h3 class="footer">Simply the easiest way to order takeaway food online</h3>
                </td>
                <td width="200">
                    <h3 class="footer">Company</h3>
                </td>
                <td width="200">
                    <h3 class="footer">Now Serving</h3>
                    <div class="location-list">
                    <c:forEach var="location" items="${locations}">
                    <p><a class="location" href="${ctx}/app/find-takeaway-food-in-${location}/loc/<util:escape value="${location}"/>">${location} Takeaway</a></p>
                    </c:forEach>
                    <c:forEach var="cuisine" items="${footerCuisines}">
                    <p><a class="location" href="${ctx}/app/find-${cuisine}-takeaway-food/csn/<util:escape value="${cuisine}"/>">${cuisine} Takeaway</a></p>
                    </c:forEach>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

