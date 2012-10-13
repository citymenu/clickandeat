
$(document).ready(function(){

    $.post( ctx+'/order/getCompletedOrder.ajax?mgn=' + (Math.random() * 99999999), {orderId: completedorderid },
        function( data ) {
            if( data.success ) {
                buildOrder(data.order);
            } else {
                   alert('success:' + data.success);
           }
        }
    );
});

// Override order behaviour
function onBeforeBuildOrder(order) {

    if( order.deliveryType == 'COLLECTION') {

        // Coordinates of restaurant
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
    }
}


// Override order config
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: false,
        showBuildOrderLink: false,
        allowRemoveItems: false,
        allowUpdateFreeItem: false,
        enableCheckoutButton: false,
        enablePaymentButton: false,
        showDiscountInformation: false,
        showAdditionalInformation: false,
        displayAdditionalInformation:true
    };
    return config;
}
