<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div id="footer">
    <div class="footer-wrapper">
        <table width="1020">
            <tr valign="top">
                <td width="500">
                    <div class="company-details">
                        <h2 class="footer">La manera más sencilla de pedir comida a domicilio.</h2>
                        <p>¡Ya estamos aquí! <span class="bolder">LlamarYComer </span> te trae la manera más sencilla
                         de disfrutar de tu comida favorita en la oficina, desde el confort de tu casa o cualquier ubicación que tu elijas.
                         Elije tu comida favorita de entre la multitud de opciones que te ofrecemos.</p>
                         <p>Ahora mismo solo tenemos establecimientos en <span class="bolder">Barcelona</span> y <span class="bolder">
                         Madrid</span>, pero estar atentos porque estamos añadiendo nuevos establecimientos constantemente.</p>

                        <h2 class="footer spacer">Italiano, Chino, Pizzeria, etc.</h2>
                        <p>LlamarYComer te ofrece una variedad extensa de las cocinas disponibles en tu zona.
                        Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor
                        in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident,
                        sunt in culpa qui officia deserunt mollit anim id est laborum.
                        </p>
                        <div class="credit-cards">
                            <div class="credit-card visa"></div>
                            <div class="credit-card mastercard"></div>
                            <div class="credit-card maestro"></div>
                            <div class="credit-card amex"></div>
                        </div>
                        <div class="copyright">
                                LlamarYcomer.com &copy;2012 - CITYMENU, S.L.
                        </div>
                    </div>
                </td>
                <td width="240">
                    <div class="company-details">
                        <h2 class="footer">Información</h2>
                        <div class="footer-list">
                            <p><a class="direct">Ayuda y preguntas frecuentes</a></p>
                            <p><a class="direct">Términos y Condiciones</a></p>
                            <p><a class="direct">Dueños de restaurantes</a></p>
                            <p><a class="direct">Revista</a></p>
                        </div>
                        <h2 class="footer spacer">Contactanos</h2>
                        <div class="footer-list">
                            <p>Calle San Fernando 20<br>Mahón 07701<br>Menorca</p>
                            <p><a class="icon-email direct">contact@llamarycomer.com</a></p>
                        </div>
                    </div>
                </td>
                <td width="180">
                    <h2 class="footer">Ahora Servimos</h2>
                    <div class="footer-list">
                    <c:forEach var="entry" items="${locations}">
                    <p><a class="direct" href="${ctx}/app/find-takeaway-food-in-${entry.value}/loc/<util:escape value="${entry.key}"/>">${entry.value} Takeaway</a></p>
                    </c:forEach>
                    </d>
                    <h2 class="footer">Cocinas</h2>
                    <div class="footer-list">
                    <c:forEach var="entry" items="${footerCuisines}">
                    <p><a class="direct" href="${ctx}/app/find-${entry.value}-takeaway-food/csn/<util:escape value="${entry.key}"/>">${entry.value} Takeaway</a></p>
                    </c:forEach>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

