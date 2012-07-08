var currentOrder;

$(document).ready(function(){
    if( orderid && orderid != '') {
        $.post( ctx+'/order/getOrder.ajax',
            function( data ) {
                if( data.success ) {
                    buildOrder(data.order);
                } else {
                    alert('success:' + data.success);
                }
            }
        );
    }
});

// Build the order display component
function buildOrder(order) {

    // Update current order object
    currentOrder = order;

    // Reset all previous order details
    $('.ordertitle').remove();
    $('.orderdeliverychoice').remove();
    $('.orderitemrow').remove();
    $('.totalitemcost').remove();
    $('.checkoutbutton').remove();
    $('.deliverywarning').remove();

    // Add the order with the restaurant name if it exists and at least one item is added
    if( order && order.orderItems.length > 0 ) {
        $('.orderheader').append('<span class=\'ordertitle\'> with {0}</span>'.format(unescapeQuotes(order.restaurantName)));
    }

    // Add the delivery options to the order if at least one item is added
    if( order && order.orderItems.length > 0 ) {
        var deliveryChecked = order? (order.deliveryType == 'DELIVERY'? ' checked': ''): ' checked';
        var collectionChecked = order? (order.deliveryType == 'COLLECTION'? ' checked': ''): '';
        var deliveryRadio = '<span class=\'deliveryradio\'><input type=\'radio\' id=\'radioDelivery\' name=\'deliveryType\' value=\'DELIVERY\'{0}> Delivery</span>'.format(deliveryChecked);
        var collectionRadio = '<span class=\'collectionradio\'><input type=\'radio\' id=\'radioCollection\' name=\'deliveryType\' value=\'COLLECTION\'{0}> Collection</span>'.format(collectionChecked);
        $('.orderdelivery').append('<div class=\'orderdeliverychoice\'>{0}{1}</div>'.format(deliveryRadio,collectionRadio));

        // Event handlers to update delivery type
        $('#radioDelivery').change(function(element){ updateDeliveryType('DELIVERY');});
        $('#radioCollection').change(function(element){ updateDeliveryType('COLLECTION');});
    }

    // Build order details if an order exists
    if( order ) {
        for (var i = order.orderItems.length - 1; i >= 0; i--) {
            var orderItem = order.orderItems[i];
            var row = '<tr class=\'orderitemrow\' valign=\'top\'><td>{0}</td><td align=\'center\'>{1}</td><td align=\'right\'>{2}{3}</td><td align=\'center\'><a href=\'#\' onclick=\"removeFromOrder(\'{4}\')\">Remove</a></td></tr>'
                .format(unescapeQuotes(orderItem.menuItemTitle),orderItem.quantity,ccy,(orderItem.cost * orderItem.quantity).toFixed(2),orderItem.menuItemId);
            $('.orderbody').prepend(row);
        };
        $('.totalcost').append('<span class=\'totalitemcost\'>{0}{1}</span>'.format(ccy,order.orderItemCost.toFixed(2)));
        if( order.orderItems.length > 0 ) {
            if(order.deliveryType == 'DELIVERY' && order.orderItemCost < minimumOrderForFreeDelivery ) {
                if(allowDeliveryOrdersBelowMinimum && deliveryCharge > 0 ) {
                    var warning = '<div class=\'deliverywarning\'>A charge of {0}{1} will be applied for delivery of this order.</div>'.format(ccy,deliveryCharge.toFixed(2));
                    $('.deliverycheck').append(warning);
                } else {
                    var additionalSpend = minimumOrderForFreeDelivery - order.orderItemCost;
                    var warning = '<div class=\'deliverywarning\'>You need to spend an additional {0}{1} to place this order for delivery.</div>'.format(ccy,additionalSpend.toFixed(2));
                    $('.deliverycheck').append(warning);
                }
            }

            $('.checkout').append('<input type=\'button\' value=\'Proceed With Order\' class=\'checkoutbutton\'>');
            $('.checkoutbutton').button();
        }
    } else {
        $('.totalcost').append('<span class=\'totalitemcost\'>' + ccy + '0.00</span>');
    }
}

// Add item to order, check that restaurant has not changed
function addToOrder(restaurantId, itemId, itemName, itemCost, quantity ) {
    if( currentOrder && currentOrder.orderItems.length > 0 && currentOrder.restaurantId != restaurantId ) {
        $('<div></div>')
            .html('<div>You already have an order with {0}. You cannot order from more than one restaurant at a time. If you add this item all of the items for your order with {0} will be removed.</div><div>Do you want to proceed?</div>'.format(unescapeQuotes(currentOrder.restaurantName)))
        	.dialog({
        	    modal:true,
        		title:'Are you sure?',
        		buttons: {
                    "Add Item Anyway": function() {
                	    $( this ).dialog( "close" );
                	    doAddToOrder(restaurantId, itemId, itemName, itemCost, quantity );
                	},
                	"Don't Add Item": function() {
                	    $( this ).dialog( "close" );
                    }
                }
            });
    } else {
        doAddToOrder(restaurantId, itemId, itemName, itemCost, quantity );
    }
}

// Add item to order update result on display
function doAddToOrder(restaurantId, itemId, itemName, itemCost, quantity ) {

    var update = {
        restaurantId: restaurantId,
        restaurantName: restaurantName,
        itemId: itemId,
        itemName: itemName,
        itemCost: itemCost,
        quantity: quantity || 1
    };

    $.post( ctx+'/order/addItem.ajax',
        {
            body: JSON.stringify(update)
        },
        function( data ) {
            if( data.success ) {
                buildOrder(data.order);
            } else {
                alert('success:' + data.success);
            }
        }
    );
}

// Remove an item from the order
function removeFromOrder(itemId, quantity ) {

    var update = {
        itemId: itemId,
        quantity: quantity || 1
    };

    $.post( ctx+'/order/removeItem.ajax', { body: JSON.stringify(update) },
        function( data ) {
            if( data.success ) {
                buildOrder(data.order);
            } else {
                alert('success:' + data.success);
            }
        }
    );
}

// Update delivery type
function updateDeliveryType(deliveryType) {
    $.post( ctx+'/order/updateDeliveryType.ajax', { deliveryType: deliveryType },
        function( data ) {
            if( data.success ) {
                buildOrder(data.order);
            } else {
                alert('success:' + data.success);
            }
        }
    );

}