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
                        <p>¡Ya estamos aquí! <span class="bolder">LlamarYComer </span>te trae la mejor variedad de comida a domicilio
                        para que la disfrutes en la oficina, en tu casa o donde más te plazca. Tan sólo entra en uno de nuestros restaurantes,
                        elige tus platos preferidos de entre la amplia gamma que te ofrecemos y en unos minutos los recibirás
                         en el lugar que nos hayas indicado. ¡Así de fácil!</p>
                         <p>Ahora mismo sólo tenemos algunos establecimientos seleccionados de <span class="bolder">Barcelona</span> y de <span class="bolder">
                         Madrid</span>, pero estate atento a nuestra web porque constantemente vamos añadiendo nuevos restaurantes.</p>

                        <h2 class="footer spacer">¿Qué te apetece comer hoy?</h2>
                        <p>Cocina china, mejicana... ¿italiana quizá? No te lo pienses más, en LlamarYComer tenemos los mejores restaurantes de
                        comida a domicilio presentes en tu zona. Una gran selección de cocina nacional e internacional que ponemos a tu disposición
                        para cuando no te apetezca cocinar. Además, en nuestra web encontrarás ofertas y promociones exclusivas sólo disponibles <span class="bolder">on line</span>. ¡Bon appétit!
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
                        <h2 class="footer spacer">Contáctanos</h2>
                        <div class="footer-list">
                            <p>Calle San Fernando 20<br>Mahón 07701<br>Menorca</p>
                            <p><a class="icon-email direct">contact@llamarYcomer.com</a></p>
                        </div>
                    </div>
                </td>
                <td width="180">
                    <h2 class="footer">Nuestras ciudades</h2>
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

