// Global order variables
var currentOrder;
var minimumOrderForFreeDelivery;
var allowDeliveryOrdersBelowMinimum;
var deliveryCharge;

// Close image
var closeImg = '<img src=\'' + ctx + '/resources/images/icons-shadowless/cross-script.png\'/>';

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
    $('.deliverychargerow').remove();
    $('.totalcost').remove();
    $('#checkout').remove();
    $('.deliverywarning').remove();

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

        // Get delivery details for restaurant
        minimumOrderForFreeDelivery = order.restaurant.deliveryOptions.minimumOrderForFreeDelivery || 0;
        allowDeliveryOrdersBelowMinimum = order.restaurant.deliveryOptions.allowDeliveryOrdersBelowMinimum || false;
        deliveryCharge = order.restaurant.deliveryOptions.deliveryCharge || 0;

        // Generate order items
        for (var i = order.orderItems.length - 1; i >= 0; i--) {
            var orderItem = order.orderItems[i];
            var row = '<tr class=\'orderitemrow\' valign=\'top\'><td width=\'65%\' class=\'orderitem ordertableseparator\'>{0} x {1}</td><td width=\'25%\' align=\'right\' class=\'orderitem ordertableseparator\'><div class=\'orderitemprice\'>{2}{3}</div></td><td width=\'10%\' align=\'center\' class=\'orderitem\'><a onclick=\"removeFromOrder(\'{4}\')\">{5}</a></td></tr>'
                .format(orderItem.quantity,unescapeQuotes(orderItem.menuItemTitle),ccy,(orderItem.cost * orderItem.quantity).toFixed(2),orderItem.menuItemId,closeImg);
            $('.orderbody').prepend(row);
        };

        // Add delivery charge if applicable
        if( order.orderItems.length > 0 && order.deliveryType == 'DELIVERY' && order.orderItemCost < minimumOrderForFreeDelivery && allowDeliveryOrdersBelowMinimum && deliveryCharge > 0 ) {
            var row = '<tr class=\'deliverychargerow\' valign=\'top\'><td width=\'65%\' class=\'deliverycharge ordertableseparator\'>Delivery charge</td><td width=\'25%\' align=\'right\' class=\'deliverycharge ordertableseparator\'><div class=\'orderitemprice\'>{0}{1}</div></td><td width=\'10%\'></td>'.format(ccy,deliveryCharge.toFixed(2));
            $('.orderbody').append(row);
        }

        // Build total item cost
        $('#ordertotal').append('<span class=\'totalcost\'>{0}{1}</span>'.format(ccy,order.totalCost.toFixed(2)));

        // Build warning about delivery or show checkout
        if( order.orderItems.length > 0 ) {

            if(order.deliveryType == 'DELIVERY' && order.orderItemCost < minimumOrderForFreeDelivery && !allowDeliveryOrdersBelowMinimum) {
                var additionalSpend = minimumOrderForFreeDelivery - order.orderItemCost;
                var warning = '<div class=\'deliverywarning\'>You need to spend an additional {0}{1} to place this order for delivery.</div>'.format(ccy,additionalSpend.toFixed(2));
                $('.deliverycheck').append(warning);
            } else {
                $('#checkoutcontainer').append('<div id=\'checkout\'><input type=\'button\' value=\'Checkout\' class=\'checkoutbutton\'></div>');
                $('.checkoutbutton').button();
                $('.checkoutbutton').click(function(){
                    checkout();
                });
            }
        }
    } else {
        $('#ordertotal').append('<span class=\'totalitemcost\'>' + ccy + ' 0.00</span>');
    }
}

// Add item to order, check that restaurant has not changed
function addToOrder(restaurantId, itemId, itemName, itemCost, quantity ) {
    if( currentOrder && currentOrder.orderItems.length > 0 && currentOrder.restaurantId != restaurantId ) {
        $('<div></div>')
            .html('<div>You already have an order with {0}. You cannot order from more than one restaurant at a time. If you add this item all of the items for your order with {0} will be removed.</div><div>Do you want to proceed?</div>'.format(unescapeQuotes(currentOrder.restaurant.name)))
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
        itemId: itemId,
        itemName: itemName,
        itemCost: itemCost,
        quantity: quantity || 1
    };

    $.post( ctx+'/order/addItem.ajax', { body: JSON.stringify(update) },
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

// Proceed to checkout
function checkout() {
    if( currentOrder && currentOrder.orderItems.length > 0 ) {
        if(currentOrder.deliveryType == 'DELIVERY' && !allowDeliveryOrdersBelowMinimum && currentOrder.orderItemCost < minimumOrderForFreeDelivery) {
            var additionalSpend = minimumOrderForFreeDelivery - currentOrder.orderItemCost;
            $('<div></div>')
                .html('<div>Your order value is below the minimum amount for delivery, you need to spend an additional {0}{1} on this order.</div>'.format(ccy,additionalSpend.toFixed(2)))
                .dialog({
                    modal:true,
                    title:'Order cost below minimum for delivery',
                    buttons: {
                        "OK": function() {
                            $( this ).dialog( "close" );
                        }
                    }
                });
        } else {
            location.href = ctx + '/secure/checkout.html';
        }
    }
}