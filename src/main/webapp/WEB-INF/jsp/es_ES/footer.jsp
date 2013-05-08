<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div id="footer">
    <div class="footer-wrapper">
        <table width="1000" class="footer-table">
            <tr valign="top">
                <td width="500" style="padding-left:10px">
                    <div class="company-details">
                        <div class="footer-h1">La manera más sencilla de pedir comida a domicilio.</div>
                        <p>¡Ya estamos aquí! <span class="bolder">LlamarYComer </span>te trae la mejor variedad de comida a domicilio
                        para que la disfrutes en la oficina, en tu casa o donde más te plazca. Tan sólo entra en uno de nuestros restaurantes,
                        elige tus platos preferidos de entre los muchos que te ofrecemos y en unos minutos
                        los recibirás en el lugar que nos hayas indicado.¡Así de fácil!</p>

                         <p>Ahora mismo sólo tenemos algunos establecimientos seleccionados de <span class="bolder">Barcelona</span>
                          y de <span class="bolder">Madrid</span>, pero estate atento a nuestra web porque constantemente vamos añadiendo
                          nuevos restaurantes.</p>

                        <div class="footer-h1 spacer">¿Qué te apetece comer hoy?</div>
                        <p>Cocina china, mejicana... ¿italiana quizá? No te lo pienses más, en LlamarYComer tenemos los mejores restaurantes de
                        comida a domicilio presentes en tu zona. Una gran selección de gastronomía nacional e internacional que ponemos a tu
                        disposición para cuando no te apetezca cocinar. Además, en nuestra web encontrarás ofertas y
                        promociones exclusivas sólo disponibles <span class="bolder">on line</span>. ¡Bon appétit!</p>
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
                            <p><a class="direct" href="${ctx}/help.html">Ayuda y preguntas frecuentes</a></p>
                            <p><a class="direct" href="${ctx}/legal.html">Términos y Condiciones</a></p>
                            <p><a class="direct" href="${resources}/docs/promotions/es_ES/bases-legales-sorteo-jamon.pdf">Bases Legales: Sorteo de un jamón</a></p>
                            <p><a class="direct">Dueños de restaurantes</a></p>
                        </div>

                        <h2 class="footer spacer">Revista - Barcelona</h2>
                        <div class="footer-list">
                            <p><a class="direct" href="${resources}/docs/magazine/es_ES/BCN_Zona1_num01.pdf" target="_blank">Zona1 - numero 1</a></p>
                            <p><a class="direct" href="${resources}/docs/magazine/es_ES/BCN_Zona2_num01.pdf" target="_blank">Zona2 - numero 1</a></p>
                        </div>
                        <h2 class="footer spacer">Revista - Madrid</h2>
                        <div class="footer-list">
                            <p><a class="direct" href="${resources}/docs/magazine/es_ES/Madrid_Zona1_num01.pdf" target="_blank">Zona1 - numero 1</a></p>
                        </div>


                        <h2 class="footer spacer">Contáctanos</h2>
                        <div class="footer-list">
                            <p>Calle San Fernando 20<br>Mahón 07701<br>Menorca</p>
                            <p><a class="icon-email direct" href="mailto:contact@llamarYcomer.com">contact@llamarYcomer.com</a></p>
                        </div>
                    </div>
                </td>
                <td width="180" style="padding-right:10px">
                    <c:forEach var="entry" items="${cuisineLocations}">
                        <div class="cuisineLocation">
                            <a class="direct" href="${ctx}/app/encontrar-comida-para-llevar-en-${entry.key.second}/loc/${entry.key.first}"><h2 class="footer">${entry.key.second}</h2></a>
                            <c:forEach var="list" items="${entry.value}">
                                <p class="locationlink"><a class="direct" href="${ctx}/app/encontrar-${list.second}-comida-para-llevar-en-${entry.key.second}/csn/${list.first}/${entry.key.first}">${list.second} a domicilio</a></p>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </td>
            </tr>
        </table>
    </div>
</div>

<div style="display:none">
    <c:forEach var="entry" items="${cuisineLocationsFull}">
        <div>
            <a href="${ctx}/app/encontrar-comida-para-llevar-en-${entry.key.second}/loc/${entry.key.first}">${entry.key.second}</a>
        </div>
        <c:forEach var="list" items="${entry.value}">
            <div><a href="${ctx}/app/encontrar-${list.second}-comida-para-llevar-en-${entry.key.second}/csn/${list.first}/${entry.key.first}">${list.second} a domicilio</a></div>
        </c:forEach>
    </c:forEach>
</div>
