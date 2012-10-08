var isValid = false;
var mapRendered = false;

$(document).ready(function(){
    updateDeliveryDisplay(deliveryType);
    validateForm();

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

// Last name validation
validators.setItem('lastName',new Validator({
    fieldName: 'lastName',
    regexp: checkoutRegexps.lastName,
    invalidText: getLabel('validation.lastName.invalidText')
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
        allowRemoveItems: false,
        allowUpdateFreeItem: true,
        enableCheckoutButton: false,
        showDiscountInformation: true,
        showAdditionalInformation: false
    };
    return config;
}


// Override order behaviour
function onBeforeBuildOrder(order) {
    canCheckout = order.canCheckout;
    updateDeliveryDisplay(order.deliveryType);
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


// Update order
function updateOrder() {
    $.post( ctx + '/updateOrder.ajax', { body: JSON.stringify(buildUpdate()) },
        function( data ) {
            if( data.success ) {
                location.href = ctx + '/buildOrder.html';
            } else {
                alert('success:' + data.success);
            }
        }
    );
}

// Proceed to payment
function proceedToPayment() {
    validateForm();
    if( isValid ) {
        $.post( ctx + '/secure/proceedToPayment.ajax', { body: JSON.stringify(buildUpdate()) },
            function( data ) {
                if( data.success ) {
                    location.href = ctx + '/secure/payment.html';
                } else {
                    showError(data.header,data.message);
                }
            }
        );
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
