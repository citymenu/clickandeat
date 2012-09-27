$(document).ready(function(){
    updateDeliveryDisplay(deliveryType);
});

// Override order config
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: true,
        allowRemoveItems: false,
        allowUpdateFreeItem: true,
        enableCheckoutButton: false,
        showDiscountInformation: true
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
    }
}

// Initialize google map
function initializeMap() {
    var mapOptions = {
        center: new google.maps.LatLng(-34.397, 150.644),
        zoom: 8,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map(
        document.getElementById("restaurant-location"),
        mapOptions
    );
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
function proceed() {
    $.post( ctx + '/secure/proceedToPayment.ajax', { body: JSON.stringify(buildUpdate()) },
        function( data ) {
            if( data.success ) {
                location.href = ctx + '/secure/payment.html';
            } else {
                alert('success:' + data.success);
            }
        }
    );
}

// Builds the update object
function buildUpdate() {

    var person = {
        firstName: $('input[name="firstName"]').val(),
        lastName: $('input[name="lastName"]').val(),
        telephone: $('input[name="telephone"]').val(),
        email: $('input[name="email"]').val()
    };

    var deliveryAddress = {
        address1: $('input[name="address1"]').val(),
        address2: $('input[name="address2"]').val(),
        address3: $('input[name="address3"]').val(),
        town: $('input[name="town"]').val(),
        region: $('input[name="region"]').val(),
        postCode: $('input[name="postCode"]').val()
    }

    var additionalInstructions = $('textarea[name="additionalInstructions"]').val()

    return {
        person: person,
        deliveryAddress: deliveryAddress,
        additionalInstructions: additionalInstructions
    };
}
