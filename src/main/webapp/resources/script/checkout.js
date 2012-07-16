$(document).ready(function(){
    displayDeliveryOptions(deliveryType);
    $('#proceedbutton').button();
    $('#proceedbutton').click(function(){
        proceed('payment');
    });
});

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

function proceed(nextPage) {

    // Get person details
    var person = {
        firstName: $('input[name="firstName"]').val(),
        lastName: $('input[name="lastName"]').val(),
        email: $('input[name="email"]').val(),
        telephone: $('input[name="telephone"]').val()
    };

    // Get delivery address details
    var deliveryAddress = {
        address1: $('input[name="address1"]').val(),
        address2: $('input[name="address2"]').val(),
        address3: $('input[name="address3"]').val(),
        town: $('input[name="town"]').val(),
        region: $('input[name="region"]').val(),
        postCode: $('input[name="postCode"]').val()
    }

    // Build post object
    var update = {
        person: person,
        deliveryAddress: deliveryAddress,
        nextPage: nextPage
    };

    $.post( ctx + '/secure/checkout.ajax', { body: JSON.stringify(update) },
        function( data ) {
            if( data.success ) {
                location.href = data.nextpage;
                alert('success');
            } else {
                alert('success:' + data.success);
            }
        }
    );

}
