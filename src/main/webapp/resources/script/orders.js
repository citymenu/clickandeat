// Global order variables
var currentOrder;
var minimumOrderForFreeDelivery;
var allowDeliveryOrdersBelowMinimum;
var deliveryCharge;

$(document).ready(function(){
    if( orderid && orderid != '') {
        $.post( ctx+'/order/getOrder.ajax?mgn=' + (Math.random() * 99999999),
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

// Returns the config for the order, can be overridden
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: true,
        allowRemoveItems: true,
        enableCheckoutButton: true
    };
    return config;
}

// Event handler for before order is built, intended to be overriden
function onBeforeBuildOrder(order,config) {
}

// Event handler for after order is built, intended to be overriden
function onAfterBuildOrder(order,config) {
}

// Order build function
function buildOrder(order) {
    var config = getOrderPanelConfig();
    onBeforeBuildOrder(order,config);
    doBuildOrder(order,config);
    onAfterBuildOrder(order,config);
}

// Build the order display component
function doBuildOrder(order,config) {

    // Update current order object
    currentOrder = order;

    // Reset all previous order details
    $('.ordertitle').remove();
    $('.orderdeliverychoice').remove();
    $('.orderitemrow').remove();
    $('.discountrow').remove();
    $('.deliverychargerow').remove();
    $('.totalcost').remove();
    $('#checkout').remove();
    $('.deliverywarning').remove();

    // Add the delivery options to the order if at least one item is added and it is enabled
    if(config.showDeliveryOptions) {
        if( order && order.orderItems.length > 0 ) {
            var deliveryChecked = order? (order.deliveryType == 'DELIVERY'? ' checked': ''): ' checked';
            var collectionChecked = order? (order.deliveryType == 'COLLECTION'? ' checked': ''): '';
            var deliveryRadio = ('<span class=\'deliveryradio\'><input type=\'radio\' id=\'radioDelivery\' name=\'deliveryType\' value=\'DELIVERY\'{0}> ' + labels['delivery'] + '</span>').format(deliveryChecked);
            var collectionRadio = ('<span class=\'collectionradio\'><input type=\'radio\' id=\'radioCollection\' name=\'deliveryType\' value=\'COLLECTION\'{0}> ' + labels['collection'] + '</span>').format(collectionChecked);
            $('.orderdelivery').append('<div class=\'orderdeliverychoice\'>{0}{1}</div>'.format(deliveryRadio,collectionRadio));

            // Event handlers to update delivery type
            $('#radioDelivery').change(function(element){ updateDeliveryType('DELIVERY',order.restaurantId);});
            $('#radioCollection').change(function(element){ updateDeliveryType('COLLECTION',order.restaurantId);});
        }
    }

    // Build order details if an order exists
    if( order ) {

        // Generate order items
        for (var i = order.orderItems.length - 1; i >= 0; i--) {
            var orderItem = order.orderItems[i];
            if(config.allowRemoveItems) {
                var closeImg = '<img src=\'' + resources + '/images/icons-shadowless/cross-script.png\' title=\'' + labels['remove-from-order'] + '\'/>';
                var row = '<tr class=\'orderitemrow\' valign=\'top\'><td width=\'65%\' class=\'orderitem ordertableseparator\'>{0} x {1}</td><td width=\'25%\' align=\'right\' class=\'orderitem ordertableseparator\'><div class=\'orderitemprice\'>{2}{3}</div></td><td width=\'10%\' align=\'center\' class=\'orderitem\'><a onclick=\"removeFromOrder(\'{4}\')\">{5}</a></td></tr>'
                    .format(orderItem.quantity,unescapeQuotes(orderItem.menuItemTitle),ccy,(orderItem.cost * orderItem.quantity).toFixed(2),orderItem.menuItemId,closeImg);
            } else {
                var row = '<tr class=\'orderitemrow\' valign=\'top\'><td width=\'65%\' class=\'orderitem ordertableseparator\'>{0} x {1}</td><td width=\'25%\' align=\'right\' class=\'orderitem ordertableseparator\'><div class=\'orderitemprice\'>{2}{3}</div></td><td width=\'10%\' align=\'center\' class=\'orderitem\'></td></tr>'
                    .format(orderItem.quantity,unescapeQuotes(orderItem.menuItemTitle),ccy,(orderItem.cost * orderItem.quantity).toFixed(2));
            }
            $('.orderbody').prepend(row);
        };

        // Add details of any discount
        order.orderDiscounts.forEach(function(orderDiscount) {
            var row = ('<tr class=\'discountrow\' valign=\'top\'><td width=\'65%\' class=\'discount ordertableseparator\'>' + orderDiscount.title + '</td><td width=\'25%\' align=\'right\' class=\'discount discounttotal ordertableseparator\'><div class=\'orderitemprice\'>-{0}{1}</div></td><td width=\'10%\'></td></tr>').format(ccy,orderDiscount.discountAmount.toFixed(2));
            $('.orderbody').append(row);
        });

        // Add delivery charge if applicable
        if( order.deliveryCost && order.deliveryCost > 0 ) {
            var row = ('<tr class=\'deliverychargerow\' valign=\'top\'><td width=\'65%\' class=\'deliverycharge ordertableseparator\'>' + labels['delivery-charge'] + '</td><td width=\'25%\' align=\'right\' class=\'deliverycharge ordertableseparator\'><div class=\'orderitemprice\'>{0}{1}</div></td><td width=\'10%\'></td></tr>').format(ccy,order.deliveryCost.toFixed(2));
            $('.orderbody').append(row);
        }

        // Build total item cost
        $('#ordertotal').append('<span class=\'totalcost\'>{0}{1}</span>'.format(ccy,order.totalCost.toFixed(2)));

        // Show warning if item cost is below minimum for delivery
        if( order.extraSpendNeededForDelivery && order.extraSpendNeededForDelivery > 0 ) {
            var warning = ('<div class=\'deliverywarning\'>' + labels['delivery-warning'] + '</div>' ).format(ccy,order.extraSpendNeededForDelivery.toFixed(2));
            $('.deliverycheck').append(warning);
        } else {
            // Show checkout button if enabled
            if(config.enableCheckoutButton) {
                $('#checkoutcontainer').append('<div id=\'checkout\'><input type=\'button\' value=\'' + labels['checkout'] + '\' class=\'checkoutbutton\'></div>');
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

// Add multiple items based on the select value
function addMultipleToOrder(restaurantId, itemNumber, itemId, itemName, itemCost ) {
    var quantity = $('#select_' + itemId ).val();
    addToOrder(restaurantId, itemNumber, itemId, itemName, itemCost, quantity);
}

// Add item to order, check that restaurant has not changed
function addToOrder(restaurantId, itemNumber, itemId, itemName, itemCost, quantity ) {
    if( currentOrder && currentOrder.orderItems.length > 0 && currentOrder.restaurantId != restaurantId ) {
        $('<div></div>')
            .html(('<div>' + labels['restaurant-warning'] + '</div>').format(unescapeQuotes(currentOrder.restaurant.name)))
        	.dialog({
        	    modal:true,
        		title:labels['are-you-sure'],
        		buttons: [{
        		    text: labels['add-item-anyway'],
        		    click: function() {
                	    $( this ).dialog( "close" );
                	    doAddToOrder(restaurantId, itemNumber, itemId, itemName, itemCost, quantity );
                	}
                },{
                    text: labels['dont-add-item'],
                    click: function() {
                	    $( this ).dialog( "close" );
                    }
                }]
            });
    } else {
        doAddToOrder(restaurantId, itemNumber, itemId, itemName, itemCost, quantity );
    }
}

// Add item to order update result on display
function doAddToOrder(restaurantId, itemNumber, itemId, itemName, itemCost, quantity ) {

    var update = {
        restaurantId: restaurantId,
        itemNumber: itemNumber,
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
function updateDeliveryType(deliveryType, restaurantId) {
    $.post( ctx+'/order/updateDeliveryType.ajax', { deliveryType: deliveryType, restaurantId: restaurantId },
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