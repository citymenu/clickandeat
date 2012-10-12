
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

