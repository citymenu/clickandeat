<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div class="no-results-wrapper" id="notregistered">
    <div class="no-results-content-wrapper">
        <div class="no-results-content">
            <h2><message:message key="search.no-restaurants-found"/></h2>
            <div class="no-results-text">Lo sentimos much&#237;simo pero todav&#237;a no tenemos restaurantes que sirvan en tu zona.
            Estamos creciendo muy r&#225;pido y a&#241;adiendo zonas nuevas a nuestra pagina constantemente.
            Por favor vuelve a visitarnos pronto y confiamos en poder ofrecer servicio en tu zona la más pronto posible.</div>
            <div class="no-results-text">Si quieres formar parte de nuestra lista de correo, por favor facil&#237;tanos tu direcci&#243;n
            de correo electr&#243;nico y una vez tengamos servicio en tu zona te mandaremos un cup&#243;n de descuento
            que uses con tu primer pedido online.</div>
            <div class="email-entry">
                <input type="text" id="email" style="width:200px; margin-right:10px;"/>
                <a class="register-button" id="#register" onclick="register()"><message:message key="button.register"/></a>
                <div class="invalid-email">Por favor introduce un correo electr&#243;nico v&#225;lido</div>
            </div>
        </div>
    </div>
</div>
<div class="no-results-wrapper" id="registered">
    <div class="registration-content-wrapper">
        <div class="registration-content">
            <h2><message:message key="registration.thank-you-for-registering"/></h2>
            <div class="no-results-text">Gracias por registrarte con nosotros. Estamos extendiendo las áreas donde ofrecemos servicio constantemente,
             en cuanto tengamos restaurantes en tu área nos pondremos en contacto contigo y te mandaremos el cupón de descuento.</div>
        </div>
    </div>
</div>