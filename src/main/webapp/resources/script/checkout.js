var orderId;
var isValid = false;
var mapRendered = false;

var defaultValidationError = getLabel('checkout.validation-error');

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

    // Add click event handler for terms and conditions
    $('.terms').click(function(){
        $.fancybox.open({
            href: ctx + '/termsAndConditions.html',
            type:'iframe',
            width:600,
            autoHeight:true,
            modal:false,
            openEffect:'none',
            closeEffect:'none'
        });
    });

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
        allowChangeLocation: true,
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
    $('#buildorder').off('click');
    $('#buildorder').click(function(){
        updateOrder();
    });
    if( order.canSubmitPayment == true) {
        $('#validation-error').hide();
        $('#validation-message').remove();
    }
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

// Removes validation errors after updating delivery
function onAfterUpdateDelivery() {
    $('#validation-error').hide();
    $('#validation-message').remove();
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
        $('#validation-error').hide();
        $('#validation-message').remove();
        $.fancybox.showLoading();
        $.post( ctx + '/order/applyVoucher.ajax', { orderId: orderId, voucherId: voucherId },
            function(data) {
                $.fancybox.hideLoading();
                if( data.success ) {
                    $('#voucherid').val('');
                    buildOrder(data.order);
                } else {
                    $('#validation-message-wrapper').append(('<span id=\'validation-message\'>{0}</div>').format(getLabel('checkout.' + data.reason)));
                    $('#validation-error').show();
                    $.scrollTo(0);
                }
            }
        );
    }
}


// Update order
function updateOrder() {
    $.fancybox.showLoading();
    $.post( ctx + '/updateOrder.ajax', { body: JSON.stringify(buildUpdate()), updateLocation: true },
        function( data ) {
            if( data.success ) {
                location.href = ctx + '/app/restaurant/' + data.restaurantId;
            } else {
                $.fancybox.hideLoading();
            }
        }
    );
}

// Proceed to payment
function payment() {

    $('#validation-error').hide();
    $('#validation-message').remove();
    validateForm();
    if( isValid ) {

        // Confirm terms and conditions accepted
        if(!$('#termsAndConditions').attr('checked')) {
            $('#validation-message-wrapper').append(('<span id=\'validation-message\'>{0}</div>').format(getLabel('checkout.validate-terms-and-conditions')));
            $('#validation-error').show();
            $.scrollTo(0);
            return;
        }

        $.fancybox.showLoading();
        $.post( ctx + '/proceedToPayment.ajax', { body: JSON.stringify(buildUpdate()) },
            function( data ) {
                if( data.success ) {
                    location.href = ctx + '/payment.html';
                } else {
                    $.fancybox.hideLoading();
                    if( data.reason != null ) {
                        var reason = data.reason;
                        $('#validation-message-wrapper').append(('<span id=\'validation-message\'>{0}</div>')
                            .format(getLabel('checkout.' + data.reason)));
                        $('#validation-error').show();
                        $.scrollTo(0);
                        getOrder();
                    }
                }
            }
        );
    } else {
        $('#validation-message-wrapper').append(('<span id=\'validation-message\'>{0}</div>').format(defaultValidationError));
        $('#validation-error').show();
        $.scrollTo(0);
    }
}

// Runs after location is updated
function onAfterLocationUpdate() {
    $('#validation-error').hide();
    $('#validation-message').remove();
    $.fancybox.showLoading();
    $.post( ctx + '/updateOrder.ajax', { body: JSON.stringify(buildUpdate()), updateLocation: false },
        function( data ) {
            $.fancybox.hideLoading();

            // Update delivery address if this is a delivery order
            var deliveryAddress = data.deliveryAddress;
            $('#address1').val(deliveryAddress.address1);
            $('#town').val(deliveryAddress.town);
            $('#region').val(deliveryAddress.region);
            $('#postCode').val(deliveryAddress.postCode);
            validateForm();

            // Show error if there is a reason why we cannot proceed
            if( !data.success && data.reason != null ) {
                var reason = data.reason;
                $('#validation-message-wrapper').append(('<span id=\'validation-message\'>{0}</div>')
                    .format(getLabel('checkout.' + data.reason)));
                $('#validation-error').show();
                $.scrollTo(0);
            }

            // Update the order panel
            getOrder();
        }
    );
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

    var termsAndConditionsAccepted = $('#termsAndConditions').attr('checked')? true: false;

    return {
        person: person,
        deliveryAddress: deliveryAddress,
        additionalInstructions: additionalInstructions,
        termsAndConditionsAccepted: termsAndConditionsAccepted
    };
}

