<%@ page language="java" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<div class="no-results-wrapper" id="notregistered">
    <div class="no-results-content-wrapper">
        <div class="no-results-content">
            <h2><message:message key="search.no-restaurants-found"/></h2>
            <div class="no-results-text">I&pos;m very sorry but I do not currently have any restaurants on this site which serve your location. Rest assured that I am constanly
             expanding the areas that this site covers wo please check back with me in a couple of months.</div>
            <div class="no-results-text">If you want to join my mailing list, please let me know your your email address and as soon as I have restaurants
            in your area I will send you a discount voucher entitling you to <b>10% off</b> your first online order.</div>
            <div class="email-entry">
                <input type="text" id="email" style="width:200px; margin-right:10px;"/>
                <a class="register-button" id="#register" onclick="register()"><message:message key="button.register"/></a>
                <div class="invalid-email">Please can you ensure that you enter a valid email address</div>
            </div>
        </div>
    </div>
</div>
<div class="no-results-wrapper" id="registered">
    <div class="registration-content-wrapper">
        <div class="registration-content">
            <h2><message:message key="registration.thank-you-for-registering"/></h2>
            <div class="no-results-text">Thank you so much for registering with my site. I am constantly expanding the areas that this site covers and as soon as I have restaurants in your
            area I will let you know and send you your discount voucher.</div>
        </div>
    </div>
</div>