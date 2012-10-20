$(document).ready(function(){

    // Hide the registered panel
    $('#registered').hide();

    // Hide the invalid email address field
    $('.invalid-email').hide();

    // Add keydown handler to email field
    $('#email').keydown(function(){
        $('.invalid-email').hide();
    });

});

// Override order config
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: false,
        showBuildOrderLink: true,
        allowRemoveItems: false,
        allowUpdateFreeItem: false,
        enableCheckoutButton: false,
        enablePaymentButton: false,
        showDiscountInformation: true,
        showAdditionalInformation: false,
        displayAdditionalInformation:true
    };
    return config;
}


// Registers when no restaurants found
function register() {
    $('.invalid-email').hide();
    var regexp = checkoutRegexps.email;
    var email = $('#email').val();
    if( !regexp.test(email)) {
        $('.invalid-email').show();
    } else {
        $.fancybox.showLoading();
        $.post( ctx + '/register/registerCustomer.ajax', { email: email, discount: 10 },
            function( data ) {
                $.fancybox.hideLoading();
                $('#notregistered').hide();
                $('#registered').show();
            }
        );
    }
}
