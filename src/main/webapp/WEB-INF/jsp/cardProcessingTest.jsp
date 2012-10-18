<%@ page language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css" media="all" href="${resources}/css/cardprocessing.css"/>
</head>

<body>

<script type="text/javascript">

$(document).ready(function(){
    $('.info').mouseenter(function() {
        $('.security-help').show()
    });

    $('.info').mouseleave(function() {
        $('.security-help').hide()
    });
});

</script>


<div class="payment-form-wrapper">
    <div class="payment-form">
        <div class="security-help">
            <div class="security-help-image"></div>
        </div>
        <div class="credit-card-wrapper">
            <div class="visa"></div>
            <div class="mastercard"></div>
        </div>
        <div class="payment-fields">
            <div class="payment-field">
                <div class="payment-header">Card number <span class="required">*</span></div>
                <div class="payment-field"><input class="card-number" type="text" autocomplete="off" name="Sis_Numero_Tarjeta" maxlength="19"/></div>
            </div>
            <div class="payment-field">
                <div class="security-code">
                    <div class="payment-header">Security code <span class="required">*</span></div>
                    <div class="payment-field">
                        <input class="security-code" type="text" maxlength="23"/>
                        <div class="info"></div>
                    </div>
                </div>
                <div class="expiry-date">
                    <div class="payment-header">Expiration date <span class="required">*</span></div>
                    <div class="payment-field">
                        <select name="Sis_Caducidad_Tarjeta_Mes">
                            <option value="1">01</option>
                            <option value="2">02</option>
                            <option value="3">03</option>
                            <option value="4">04</option>
                            <option value="5">05</option>
                            <option value="6">06</option>
                            <option value="7">07</option>
                            <option value="8">08</option>
                            <option value="9">09</option>
                            <option value="10">10</option>
                            <option value="11">11</option>
                            <option value="12">12</option>
                        </select>
                        <select name="expiry-year">
                            <option value="2012">2012</option>
                            <option value="2013">2013</option>
                            <option value="2014">2014</option>
                            <option value="2015">2015</option>
                        </select>
                    </div>
                </div>

                <div class="error-container">
                    <div class="error-text">Please enter a valid credit card number.</div>
                </div>

            </div>

            <div class="payment-footer">
                <a class="payment-button">Continue</a>
            </div>
        </div>
    </div>
</div>

</body>