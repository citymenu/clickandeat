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
        allowUpdateFreeItem: true,
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
    $('.freeitem').remove();
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
                var row = '<tr class=\'orderitemrow\' valign=\'top\'><td width=\'65%\' class=\'orderitem ordertableseparator\'>{0}</td><td width=\'25%\' align=\'right\' class=\'orderitem ordertableseparator\'><div class=\'orderitemprice\'>{1}{2}</div></td><td width=\'10%\' align=\'center\' class=\'orderitem\'><a onclick=\"removeFromOrder(\'{3}\')\">{4}</a></td></tr>'
                    .format(buildDisplay(orderItem),ccy,orderItem.formattedCost,orderItem.orderItemId,closeImg);
            } else {
                var row = '<tr class=\'orderitemrow\' valign=\'top\'><td width=\'65%\' class=\'orderitem ordertableseparator\'>{0}</td><td width=\'25%\' align=\'right\' class=\'orderitem ordertableseparator\'><div class=\'orderitemprice\'>{1}{2}</div></td><td width=\'10%\' align=\'center\' class=\'orderitem\'></td></tr>'
                    .format(buildDisplay(orderItem),ccy,orderItem.formattedCost);
            }
            $('.orderbody').prepend(row);
        };

        // Add details of any free item discounts
        order.orderDiscounts.forEach(function(orderDiscount) {
            if( orderDiscount.discountType == 'DISCOUNT_FREE_ITEM' ) {
                if( config.allowUpdateFreeItem ) {
                    var selectBox = ('<select class=\'freeitemselect\' id=\'{0}\'>').format(orderDiscount.discountId);
                    selectBox += ('<option value = \'\'>{0}</option>').format(labels['no-thanks']);
                    orderDiscount.freeItems.forEach(function(freeItem) {
                        if( orderDiscount.selectedFreeItem == freeItem ) {
                            selectBox += ('<option value=\'{0}\' selected>{0}</option>').format(freeItem);
                        } else {
                            selectBox += ('<option value=\'{0}\'>{0}</option>').format(freeItem);
                        }
                    });
                    selectBox += '</select>';
                    var div = ('<div class=\'freeitem\'><div class=\'freeitemtitle\'>{0}:</div><div class=\'freeitemselect\'>{1}</div></div>').format(orderDiscount.title,selectBox);
                    $('#freeitems').append(div);
                    $('#' + orderDiscount.discountId).change(function(){
                        var discountId = $(this).attr('id');
                        var freeItem = $(this).val();
                        updateFreeItem(discountId,freeItem);
                    });
                } else {
                    if( orderDiscount.selectedFreeItem && orderDiscount.selectedFreeItem != '') {
                        var row = ('<tr class=\'orderitemrow\' valign=\'top\'><td width=\'65%\' class=\'orderitem ordertableseparator\'>{0} ({1})</td><td width=\'25%\' align=\'right\' class=\'orderitem ordertableseparator\'><div class=\'orderitemprice\'>{2}{3}</div></td><td width=\'10%\'></td></tr>').format(orderDiscount.selectedFreeItem,labels['free'],ccy,orderDiscount.formattedAmount);
                        $('.orderbody').append(row);
                    }
                }
            }
        });

        // Add details of any cash discounts
        order.orderDiscounts.forEach(function(orderDiscount) {
            if( orderDiscount.discountType != 'DISCOUNT_FREE_ITEM' ) {
                var row = ('<tr class=\'discountrow\' valign=\'top\'><td width=\'65%\' class=\'discount ordertableseparator\'>{0}</td><td width=\'25%\' align=\'right\' class=\'discount discounttotal ordertableseparator\'><div class=\'orderitemprice\'>-{1}{2}</div></td><td width=\'10%\'></td></tr>').format(orderDiscount.title,ccy,orderDiscount.formattedAmount);
                $('.orderbody').append(row);
            }
        });

        // Add delivery charge if applicable
        if( order.deliveryCost && order.deliveryCost > 0 ) {
            var row = ('<tr class=\'deliverychargerow\' valign=\'top\'><td width=\'65%\' class=\'deliverycharge ordertableseparator\'>' + labels['delivery-charge'] + '</td><td width=\'25%\' align=\'right\' class=\'deliverycharge ordertableseparator\'><div class=\'orderitemprice\'>{0}{1}</div></td><td width=\'10%\'></td></tr>').format(ccy,order.formattedDeliveryCost);
            $('.orderbody').append(row);
        }

        // Build total item cost
        $('#ordertotal').append('<span class=\'totalcost\'>{0}{1}</span>'.format(ccy,order.formattedTotalCost));

        // Show warning if item cost is below minimum for delivery
        if( order.extraSpendNeededForDelivery && order.extraSpendNeededForDelivery > 0 ) {
            var warning = ('<div class=\'deliverywarning\'>' + labels['delivery-warning'] + '</div>' ).format(ccy,order.formattedExtraSpendNeededForDelivery);
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

// Displays an order item
function buildDisplay(orderItem) {
    var display = orderItem.quantity + ' x ' + unescapeQuotes(orderItem.menuItemTitle);
    if( orderItem.menuItemTypeName ) {
        display += ' (' + unescapeQuotes(orderItem.menuItemTypeName) + ')';
    }
    if( orderItem.menuItemSubTypeName ) {
        display += ' (' + unescapeQuotes(orderItem.menuItemSubTypeName) + ')';
    }
    orderItem.additionalItems.forEach(function(additionalItem){
        display += ('<div class=\'additionalitem\'>-{0}</div>').format(unescapeQuotes(additionalItem));
    });
    return ('<div>{0}</div>').format(display);
}

// Add multiple items based on the select value
function addMultipleToOrder(restaurantId, itemId, itemType, itemSubType, additionalItemArray, additionalItemLimit, additionalItemCost ) {
    var quantity;
    if( itemType ) {
        quantity = $('#select_' + itemId + '_' + itemType.replace(/\s/g,'_')).val();
    } else if (itemSubType) {
        quantity = $('#select_' + itemId + '_' + itemSubType.replace(/\s/g,'_')).val();
    } else {
        quantity = $('#select_' + itemId ).val();
    }

    // Check restaurant with callback on restaurant id
    restaurantCheck(restaurantId, function(){
        doAddToOrderCheck(restaurantId, itemId, itemType, itemSubType, additionalItemArray, additionalItemLimit, additionalItemCost, quantity);
    });
}

// Confirm if order should proceed
function restaurantCheck(restaurantId, callback ) {
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
                	    callback();
                	}
                },{
                    text: labels['dont-add-item'],
                    click: function() {
                	    $( this ).dialog( "close" );
                    }
                }]
            });
    } else {
        callback();
    }

}

// Add item to order, check if need to display additional item dialog
function doAddToOrderCheck(restaurantId, itemId, itemType, itemSubType, additionalItemArray, additionalItemLimit, additionalItemCost, quantity ) {
    if( additionalItemArray.length > 0 ) {
        buildAdditionalItemDialog(restaurantId, itemId, itemType, itemSubType, additionalItemArray, additionalItemLimit, additionalItemCost, quantity );
    } else {
        doAddToOrder(restaurantId, itemId, itemType, itemSubType, [], quantity )
    }
}

// Build dialog to show additional choices for a menu item
function buildAdditionalItemDialog(restaurantId, itemId, itemType, itemSubType, additionalItemArray, additionalItemLimit, additionalItemCost, quantity ) {

    var selectedItems = new HashTable();
    var html = '';
    var itemLimit = additionalItemLimit? additionalItemLimit: 0;

    // Build checkboxes for each additional item
    additionalItemArray.forEach(function(additionalItem){
        var additionalItemArray = additionalItem.split('%%%');
        var additionalItemCost = additionalItemArray[1];
        var itemDiv = ('<div class=\'additionalItem\'><input type=\'checkbox\' class=\'itemcheckbox\' id=\'{0}\'/>{1}<span class=\'additionalitemcost\'>{2}</span></div>')
                .format(additionalItemArray[0],unescapeQuotes(additionalItemArray[0]),(additionalItemArray[1] != 'null'? ' (' + ccy + additionalItemArray[1] + ')': ''));
        html += itemDiv;
    });

    // Placeholder for item count warning
    html += '<div id=\'itemcountwarning\'></div>';

    // Generate the dialog
    $('<div id=\'additionalItemDialog\'></div>')
        .html(html)
        .dialog({
            modal:true,
            title:labels['choose-additional'],
            buttons: [{
                text: labels['done'],
                id: 'button-done',
                click: function() {
                    doAddToOrder(restaurantId, itemId, itemType, itemSubType, selectedItems.keys(), quantity );
                    $( this ).dialog( "close" );
                }
            },{
                text: labels['cancel'],
                click: function() {
                    $( this ).dialog( "close" );
                }
            }],
            close: function() {
                $(this).remove();
            }
        });

    // Handler to maintain the selected additional items
    $('.itemcheckbox').change(function(){
        if($(this).is(':checked')) {
            selectedItems.setItem($(this).attr('id'), "");
        } else {
            selectedItems.removeItem($(this).attr('id'));
        }

        $('.itemcountwarning').remove();
        if( itemLimit > 0 && selectedItems.size() > itemLimit ) {
            $('#itemcountwarning').append(('<div class=\'itemcountwarning\'>{0}</div>').format(labels['additional-item-limit']).format(itemLimit));
            $('#button-done').button('disable');
        } else {
            $('#button-done').button('enable');
        }

    });
}

// Add item to order update result on display
function doAddToOrder(restaurantId, itemId, itemType, itemSubType, additionalItems, quantity ) {

    var update = {
        restaurantId: restaurantId,
        itemId: itemId,
        itemType: unescape(itemType),
        itemSubType: unescape(itemSubType),
        additionalItems: unescapeArray(additionalItems),
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

// Add a special offer item to the order
function addSpecialOfferToOrder(restaurantId, specialOfferId, specialOfferItemsArray ) {

    // Decode and build special offer items array
    var specialOfferItems = [];
    specialOfferItemsArray.forEach(function(specialOfferItemStr){

        var itemArray = specialOfferItemStr.split('%%%');
        var itemChoiceArrayStr = itemArray[2];
        var itemChoices = [];

        itemChoiceArrayStr.split('$$$').forEach(function(itemChoiceStr){
            var itemChoice = new Object({
                text:itemChoiceStr
            });
            itemChoices.push(itemChoice);
        });

        var specialOfferItem = new Object({
            title: (itemArray[0] == 'null'? null: unescapeQuotes(itemArray[0])),
            description: (itemArray[1] == 'null'? null: unescapeQuotes(itemArray[1])),
            itemChoices: itemChoices
        });
        specialOfferItems.push(specialOfferItem);
    });

    // If all special offer items only have one choice, add to order now
    var quantity = $('#select_' + specialOfferId).val();

    // Check restaurant with callback to add to order
    restaurantCheck(restaurantId, function(){
        doAddSpecialOfferToOrderCheck(restaurantId, specialOfferId, specialOfferItems, quantity);
    });

}

// Either build select dialog or proceed directly to add special offer to order
function doAddSpecialOfferToOrderCheck(restaurantId, specialOfferId, specialOfferItems, quantity) {

    var singleChoiceOnly = true;
    specialOfferItems.forEach(function(specialOfferItem){
        if( specialOfferItem.itemChoices.length > 1 ) {
            singleChoiceOnly = false;
        }
    });

    // If only one choice for each option, add to order now
    if( singleChoiceOnly ) {
        var itemChoices = [];
        specialOfferItems.forEach(function(specialOfferItem){
            itemChoices.push(specialOfferItem.itemChoices[0].text);
        });
        doAddSpecialOfferToOrder(restaurantId, specialOfferId, itemChoices, quantity);
    }
    else {

        // Build dialog to display items and choices
        var html = '';
        var specialOfferItemIndex = 0;
        specialOfferItems.forEach(function(specialOfferItem){
            var specialOfferItemContent = ('<div class=\'specialofferitemtitle\'>{0}</div>').format(unescapeQuotes(specialOfferItem.title));
            if( specialOfferItem.description ) {
                specialOfferItemContent += ('<div class=\'specialofferitemdescription\'>{0}</div>').format(unescapeQuotes(specialOfferItem.description));
            }
            var selectBox;
            if( specialOfferItem.itemChoices.length == 1 ) {
                selectBox = ('<div class=\'specialofferitemchoice\'>{0}</div>').format(unescapeQuotes(specialOfferItem.itemChoices[0].text));
            } else {
                var selectOptions = '';
                specialOfferItem.itemChoices.forEach(function(itemChoice){
                    selectOptions += ('<option value=\'{0}\'>{1}</option>').format(itemChoice.text, unescapeQuotes(itemChoice.text));
                });
                selectBox = ('<div class=\'specialofferitemchoice\'><select id=\'specialOfferItemSelect_{0}\'>{1}</select></div>').format(specialOfferItemIndex, selectOptions);
            }
            specialOfferItemContent += selectBox;
            html += ('<div class=\'specialofferitem\'>{0}</div>').format(specialOfferItemContent);
            specialOfferItemIndex++;
        });

        // Generate the dialog
        $('<div id=\'specialOfferItemDialog\'></div>')
            .html(html)
            .dialog({
                modal:true,
                title:labels['special-offer-choices'],
                buttons: [{
                    text: labels['done'],
                    id: 'button-done',
                    click: function() {
                        var itemChoices = [];
                        for( i = 0; i < specialOfferItems.length; i++) {
                            var itemSelect = $('#specialOfferItemSelect_' + i );
                            if( itemSelect ) {
                                itemChoices.push(itemSelect.val());
                            } else {
                                itemChoices.push(specialOfferItems[i].itemChoices[0].text);
                            }
                        }
                        doAddSpecialOfferToOrder(restaurantId, specialOfferId, itemChoices, quantity);
                        $( this ).dialog( "close" );
                    }
                },{
                    text: labels['cancel'],
                    click: function() {
                        $( this ).dialog( "close" );
                    }
                }],
                close: function() {
                    $(this).remove();
                }
            });
    }
}

// Add the special offer item to the order
function doAddSpecialOfferToOrder(restaurantId, specialOfferId, itemChoices, quantity ) {

    var update = {
        restaurantId: restaurantId,
        specialOfferId: specialOfferId,
        itemChoices: unescapeArray(itemChoices),
        quantity: quantity || 1
    };

    $.post( ctx+'/order/addSpecialOffer.ajax', { body: JSON.stringify(update) },
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
function removeFromOrder(orderItemId, quantity ) {

    var update = {
        orderItemId: orderItemId,
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

// Update a selected free item in a discount option
function updateFreeItem(discountId,freeItem) {
    var update = {
        discountId: discountId,
        freeItem: freeItem
    };

    $.post( ctx+'/order/updateFreeItem.ajax', { body: JSON.stringify(update) },
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

// Unescapes string values
function unescape(str) {
    if(!str || str == '') {
        return str;
    }
    while(str.indexOf('_') != -1) {
        str = str.replace('_',' ');
    }
    return unescapeQuotes(str);
}

// Unescapes an array of string values
function unescapeArray(arr) {
    var ret = [];
    arr.forEach(function(entry){
        ret.push(unescape(entry));
    });
    return ret;
}