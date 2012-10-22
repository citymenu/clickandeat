<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div class="no-results-wrapper" id="notregistered">
    <div class="no-results-content-wrapper">
        <div class="no-results-content">
            <h2><message:message key="search.no-restaurants-found"/></h2>
            <div class="no-results-text">Sorry we do not currently have any restaurants on our site which serve your location but we are constantly expanding the
            areas that we cover so please check back with us in a couple of months.</div>
            <div class="no-results-text">If you want to join our mailing list, please let us know your your email address and as soon as we have restaurants
            in your area we will send you a discount voucher entitling you to <b>10% off</b> your first online order with us.</div>
            <div class="email-entry">
                <input type="text" id="email" style="width:200px; margin-right:10px;"/>
                <a class="register-button" id="#register" onclick="register()"><message:message key="button.register"/></a>
                <div class="invalid-email">Please enter a valid email address</div>
            </div>
        </div>
    </div>
</div>
<div class="no-results-wrapper" id="registered">
    <div class="registration-content-wrapper">
        <div class="registration-content">
            <h2><message:message key="registration.thank-you-for-registering"/></h2>
            <div class="no-results-text">Thank you for registering with us. We are constantly expanding the areas that we cover and as soon as we have restaurants in your
            area we will let you know and send you your discount voucher.</div>
        </div>
    </div>
</div>