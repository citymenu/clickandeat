var orderId;
var isValid = false;
var mapRendered = false;

$(document).ready(function(){

    $('#validation-error').hide();

    updateDeliveryDisplay(deliveryType);
    validateForm();

    // Change handler to remove invalid voucher warning
    $('#voucherid').keydown(function(){
        $('.invalid-voucher').remove();
    });

    // Add validation event handlers
    validators.each(function(fieldName,validator){
        $('#' + fieldName).change(function(){
            validateForm();
        });

        $('#' + fieldName).keyup(function(){
            validateForm();
        });
    });

    // Build collection display for collection deliveries
    if( deliveryType == 'COLLECTION' ) {
        initializeMap();
    }

});


// Updates validation on all form fields
function validateForm() {
    isValid = true;
    validators.each(function(fieldName,validator){
        if( $('#' + fieldName).is(":visible")) {
            if( !validator.validate()) {
                isValid = false;
            };
        }
    });
}


// Validation entries
var validators = new HashTable();

// First name validation
validators.setItem('firstName',new Validator({
    fieldName: 'firstName',
    regexp: checkoutRegexps.firstName,
    invalidText: getLabel('validation.firstName.invalidText')
}));

// Telephone number validation
validators.setItem('telephone',new Validator({
    fieldName: 'telephone',
    regexp: checkoutRegexps.telephone,
    invalidText: getLabel('validation.telephone.invalidText')
}));

// Email address validation
validators.setItem('email',new Validator({
    fieldName: 'email',
    regexp: checkoutRegexps.email,
    invalidText: getLabel('validation.email.invalidText')
}));

// Address line one validation
validators.setItem('address1',new Validator({
    fieldName: 'address1',
    regexp: checkoutRegexps.address1,
    invalidText: getLabel('validation.address1.invalidText')
}));

// Postcode validation
validators.setItem('postCode',new Validator({
    fieldName: 'postCode',
    regexp: checkoutRegexps.postCode,
    invalidText: getLabel('validation.postCode.invalidText')
}));


// Override order config
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: true,
        showBuildOrderLink: true,
        allowRemoveItems: true,
        allowUpdateFreeItem: true,
        enableCheckoutButton: false,
        enablePaymentButton: true,
        showDiscountInformation: true,
        showAdditionalInformation: false,
        displayAdditionalInformation:false
    };
    return config;
}


// Override order behaviour
function onBeforeBuildOrder(order) {
    orderId = order.orderId;
    canCheckout = order.canCheckout;
    updateDeliveryDisplay(order.deliveryType);
}

// Override after build order
function onAfterBuildOrder(order) {
    // Override build order click event
    $('#buildorder').click(function(){
        updateOrder();
    });
}

// Updates display based on delivery type
function updateDeliveryDisplay(deliveryType) {
    if(deliveryType == 'DELIVERY') {
        $('#collection-details').hide();
        $('#delivery-details').show();
    } else {
        $('#delivery-details').hide();
        $('#collection-details').show();
        initializeMap();
    }
}

// Show restaurant map
function initializeMap() {

    // Do nothing if already rendered
    if( mapRendered ) {
        return;
    }

    // Coordinates of user and restaurant
    var restaurantLatlng = new google.maps.LatLng(coordinates[0],coordinates[1]);

    var mapOptions = {
        center: restaurantLatlng,
        zoom: 15,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    // Display map
    var map = new google.maps.Map(document.getElementById("restaurant-location"),mapOptions);

    // Add restaurant marker
    var restaurantMarker = new google.maps.Marker({
        position: restaurantLatlng,
        icon: resources + '/images/markers/blue_MarkerB.png',
        map: map
    });

    mapRendered = true;
}


// Apply voucher
function applyVoucher() {
    var voucherId = $('#voucherid').val();
    if( voucherId != '' ) {
        $('.invalid-voucher').remove();
        $.fancybox.showLoading();
        $.post( ctx + '/order/applyVoucher.ajax', { orderId: orderId, voucherId: voucherId },
            function(data) {
                $.fancybox.hideLoading();
                if( data.success ) {
                    $('#voucherid').val('');
                    buildOrder(data.order);
                } else {
                    var reason = data.reason;
                    $('#voucher-validation').append(('<div class=\'invalid-voucher\'>{0}</div>').format(getLabel('checkout.' + reason)));
                }
            }
        );
    }
}


// Update order
function updateOrder() {
    $.fancybox.showLoading();
    $.post( ctx + '/updateOrder.ajax', { body: JSON.stringify(buildUpdate()) },
        function( data ) {
            if( data.success ) {
                location.href = ctx + '/buildOrder.html';
            } else {
                $.fancybox.hideLoading();
            }
        }
    );
}

// Proceed to payment
function payment() {
    validateForm();
    if( isValid ) {
        $('.checkout-validation-error').remove();
        $('.invalid-voucher').remove();
        $('#validation-error').hide();
        $.fancybox.showLoading();
        $.post( ctx + '/proceedToPayment.ajax', { body: JSON.stringify(buildUpdate()) },
            function( data ) {
                if( data.success ) {
                    location.href = ctx + '/payment.html';
                } else {
                    $.fancybox.hideLoading();
                    if( data.reason != null ) {
                        var reason = data.reason;
                        $('#checkout-validation').append(('<div class=\'checkout-validation-error\'>{0}</div>').format(getLabel('checkout.' + reason)));
                        $.scrollTo(0);
                    } else {
                        alert(data.success);
                    }
                }
            }
        );
    } else {
        $('#validation-error').show();
        $.scrollTo(0);
    }

}

// Builds a warning dialog
function showError(header,message) {
    var subheader = ('<div class=\'warning-container\'>{0}</div>').format(message);
    var container = ('<div class=\'dialog-container\'><div class=\'dialog-header\'><h2>{0}</h2></div><div class=\'dialog-subheader\'>{1}</div></div>')
        .format(header,subheader);

    $.fancybox.open({
        type: 'html',
        content: container,
        modal:false,
        autoSize:false,
        height: 200,
        width: 450,
        openEffect:'none',
        closeEffect:'none'
    });
}

// Builds the update object
function buildUpdate() {

    var person = {
        firstName: $('#firstName').val(),
        lastName: $('#lastName').val(),
        telephone: $('#telephone').val(),
        email: $('#email').val()
    };

    var deliveryAddress = {
        address1: $('#address1').val(),
        town: $('#town').val(),
        region: $('#region').val(),
        postCode: $('#postCode').val()
    }

    var additionalInstructions = $('#additionalInstructions').val()

    return {
        person: person,
        deliveryAddress: deliveryAddress,
        additionalInstructions: additionalInstructions
    };
}
