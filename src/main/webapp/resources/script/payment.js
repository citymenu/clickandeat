// Show error if present
$(document).ready(function(){
    if( error && error != '' ) {

        var header = ('<div class=\'dialog-header\'><h2>{0}</h2></div>').format(getLabel('payment.payment-error-header'));
        var content = ('<div class=\'dialog-warning-wrapper\'><div>{0}</div>').format(error);
        var container = ('<div class=\'dialog-wrapper\'>{0}{1}</div>').format(header,content);

        $.fancybox.open({
            type: 'html',
            content: container,
            autoSize:false,
            width:400,
            autoHeight:true,
            modal:false,
            openEffect:'none',
            closeEffect:'none'
        });
    }
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

