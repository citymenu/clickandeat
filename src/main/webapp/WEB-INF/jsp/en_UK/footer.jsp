<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div id="footer">
    <div class="footer-wrapper">
        <table width="1020">
            <tr valign="top">
                <td width="500">
                    <div class="company-details">
                        <h2 class="footer">Simply the easiest way to order takeaway food online</h2>
                        <p>Lorem ipsum dolor sit amet, consectetur <span class="bolder">take away</span> adipisicing <span class="bolder">collection</span>
                        elit, sed do eiusmod tempor <span class="bolder">Madrid</span> ut labore et <span class="bolder">Barcelona</span> magna aliqua.Ut
                        enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor
                        in reprehenderit in voluptate.</p>
                        <h2 class="footer spacer">Italian, Chinese takeaway, Pizza delivery</h2>
                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                        Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor
                        in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident,
                        sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
                        <div class="credit-cards">
                            <div class="credit-card visa"></div>
                            <div class="credit-card mastercard"></div>
                            <div class="credit-card maestro"></div>
                            <div class="credit-card amex"></div>
                        </div>
                        <div class="copyright">
                            &copy;2012 llamarycomer | registered address of company
                        </div>
                    </div>
                </td>
                <td width="240">
                    <div class="company-details">
                        <h2 class="footer">About Us</h2>
                        <div class="footer-list">
                            <p><a class="direct">Help/FAQ</a></p>
                            <p><a class="direct">Terms &amp; conditions</a></p>
                            <p><a class="direct">Restaurant owners</a></p>
                            <p><a class="direct">Magazine</a></p>
                        </div>
                        <h2 class="footer spacer">Contact Us</h2>
                        <div class="footer-list">
                            <p>120 Street Name<br>Barcelona 08009<br>91 665 432</p>
                            <p><a class="icon-email direct">contact@llamarycomer.com</a></p>
                        </div>
                    </div>
                </td>
                <td width="180">
                    <h2 class="footer">Now Serving</h2>
                    <div class="footer-list">
                    <c:forEach var="location" items="${locations}">
                    <p><a class="direct" href="${ctx}/app/find-takeaway-food-in-${location}/loc/<util:escape value="${location}"/>">${location} Takeaway</a></p>
                    </c:forEach>
                    <c:forEach var="cuisine" items="${footerCuisines}">
                    <p><a class="direct" href="${ctx}/app/find-${cuisine}-takeaway-food/csn/<util:escape value="${cuisine}"/>">${cuisine} Takeaway</a></p>
                    </c:forEach>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

