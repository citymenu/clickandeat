$(document).ready(function(){
    displayDeliveryOptions(deliveryType);
    $('#proceedbutton').button();
    $('#proceedbutton').click(function(){
        proceed();
    });
    $('#updateorderbutton').button();
    $('#updateorderbutton').click(function(){
        updateOrder();
    });
});

// Override order config
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: true,
        allowRemoveItems: false,
        enableCheckoutButton: false
    };
    return config;
}


// Override order behaviour
function onBeforeBuildOrder(order) {
    displayDeliveryOptions(order.deliveryType);
}

// Updates display based on delivery type
function displayDeliveryOptions(deliveryType) {
    if(deliveryType == 'DELIVERY') {
        $('#collectiontime').hide();
        $('#deliverytime').show();
    } else {
        $('#deliverytime').hide();
        $('#collectiontime').show();
    }
}

// Update order
function updateOrder() {

    // Build post object
    var update = {
        person: getPersonDetails(),
        deliveryAddress: getDeliveryAddress()
    };

    $.post( ctx + '/updateOrder.ajax', { body: JSON.stringify(update) },
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

    var person = getPersonDetails();
    var deliveryAddress = getDeliveryAddress();

    // Build post object
    var update = {
        person: person,
        deliveryAddress: deliveryAddress
    };

    $.post( ctx + '/secure/proceedToPayment.ajax', { body: JSON.stringify(update) },
        function( data ) {
            if( data.success ) {
                location.href = ctx + '/secure/payment.html';
            } else {
                alert('success:' + data.success);
            }
        }
    );
}

// Extract the person details from the form
function getPersonDetails() {
    var person = {
        firstName: $('input[name="firstName"]').val(),
        lastName: $('input[name="lastName"]').val(),
        telephone: $('input[name="telephone"]').val(),
        email: $('input[name="email"]').val()
    };
    return person;
}

// Extract the delivery address details from the form
function getDeliveryAddress() {
    var deliveryAddress = {
        address1: $('input[name="address1"]').val(),
        address2: $('input[name="address2"]').val(),
        address3: $('input[name="address3"]').val(),
        town: $('input[name="town"]').val(),
        region: $('input[name="region"]').val(),
        postCode: $('input[name="postCode"]').val()
    }
    return deliveryAddress;
}
