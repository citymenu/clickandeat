// Global order variables
var currentOrder;
var minimumOrderForFreeDelivery;
var allowDeliveryOrdersBelowMinimum;
var deliveryCharge;

// Global delivery variables
var deliveryType;
var days;
var deliveryTimes;
var collectionTimes;
var deliveryDayOfWeek;
var deliveryTimeOfDay;
var openForDelivery;
var openForCollection;

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

$(document).ajaxStart(function(){
    $.fancybox.showLoading();
});

$(document).ajaxComplete(function(){
    $.fancybox.hideLoading();
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
    $('.delivery-wrapper').remove();
    $('.order-item-wrapper').remove();
    $('.discountrow').remove();
    $('.deliverychargerow').remove();
    $('.order-totalcost').remove();
    $('.order-free-item-wrapper').remove();
    $('#checkout').remove();
    $('.delivery-warning-wrapper').remove();

    // Add the delivery options to the order if at least one item is added and it is enabled
    if( order ) {
        var deliveryDay, deliveryTime, orderType;
        var expectedTime = (order.deliveryType == 'DELIVERY'? order.expectedDeliveryTime: order.expectedCollectionTime);
        var orderType = (order.deliveryType == 'DELIVERY'? getLabel('order.order-for-delivery'): getLabel('order.order-for-collection'));
        if( !expectedTime ) {
            deliveryDayOfWeek = null;
            deliveryTimeOfDay = null;
            deliveryDay = getLabel('weekday.today');
            deliveryTime = getLabel('time.asap');
        } else {
            var time = new Date(expectedTime);
            deliveryDayOfWeek = (time.getDay() == 0? 7: time.getDay());
            deliveryTimeOfDay = (time.getHours() < 10? '0' + time.getHours(): time.getHours()) + ':' + (time.getMinutes() < 10? '0' + time.getMinutes(): time.getMinutes());
            deliveryDay = (deliveryDayOfWeek == new Date().getDay()? getLabel('weekday.today'): getLabel('weekday.day-of-week-'+deliveryDayOfWeek));
            deliveryTime = deliveryTimeOfDay;
        }

        var link = (config.showDeliveryOptions ? '<a id=\'deliveryedit\' class=\'order-button add-button unselectable\'>' + getLabel('button.change') + '</a>' : '');
        var deliveryContainer = ('<div class=\'delivery-wrapper\'><table width=\'236\'><tr valign=\'top\'><td width=\'170\'><div class=\'delivery-title\'>{0}:</div><div class=\'delivery-header\'>{1} - {2}</div></td><td width=\'66\' align=\'right\'>{3}</td></tr></table></div>')
            .format(orderType,deliveryDay,deliveryTime,link);
        $('.order-delivery-wrapper').append(deliveryContainer);
        if( config.showDeliveryOptions ) {
            $('#deliveryedit').click(function(){
                deliveryEdit();
            });
        }
    }

    // Build order details if an order exists
    if( order ) {

        // Generate order items
        for (var i = order.orderItems.length - 1; i >= 0; i--) {
            var orderItem = order.orderItems[i];
            if(config.allowRemoveItems) {
                var row = ('<div class=\'order-item-wrapper\'><table width=\'236\'><tr valign=\'top\'><td width=\'146\'>{0}</td><td width=\'55\' align=\'right\'>{1}{2}</td><td width=\'30\' align=\'left\'><a onclick=\"removeFromOrder(\'{3}\')\"><div class=\'order-remove-item\'></div></a></td></tr></table></div>')
                    .format(buildDisplay(orderItem),ccy,orderItem.formattedCost,orderItem.orderItemId);
            } else {
                var row = '<div class=\'order-item-wrapper\'><table width=\'236\'><tr valign=\'top\'><td width=\'146\'>{0}</td><td width=\'55\' align=\'right\'>{1}{2}</td><td width=\'30\'></td></tr>'
                    .format(buildDisplay(orderItem),ccy,orderItem.formattedCost);
            }
            $('#order-item-contents').prepend(row);
        };

        // Add details of any free item discounts
        order.orderDiscounts.forEach(function(orderDiscount) {
            if( orderDiscount.discountType == 'DISCOUNT_FREE_ITEM' ) {
                if( config.allowUpdateFreeItem ) {
                    var selectBox = ('<select class=\'freeitemselect\' id=\'{0}\'>').format(orderDiscount.discountId);
                    selectBox += ('<option value = \'\'>{0}</option>').format(getLabel('order.no-thanks'));
                    orderDiscount.freeItems.forEach(function(freeItem) {
                        if( orderDiscount.selectedFreeItem == freeItem ) {
                            selectBox += ('<option value=\'{0}\' selected>{0}</option>').format(freeItem);
                        } else {
                            selectBox += ('<option value=\'{0}\'>{0}</option>').format(freeItem);
                        }
                    });
                    selectBox += '</select>';
                    var div = ('<div class=\'order-free-item-wrapper\'><div class=\'order-free-item-title\'>{0}:</div><div class=\'order-free-item-select\'>{1}</div></div>').format(orderDiscount.title,selectBox);
                    $('#freeitems').append(div);
                    $('#' + orderDiscount.discountId).change(function(){
                        var discountId = $(this).attr('id');
                        var freeItem = $(this).val();
                        updateFreeItem(discountId,freeItem);
                    });
                } else {
                    if( orderDiscount.selectedFreeItem && orderDiscount.selectedFreeItem != '') {
                        var row = ('<div class=\'order-item-wrapper\'><table width=\'236\'><trvalign=\'top\'><td width=\'146\'>{0} ({1})</td><td width=\'55\' align=\'right\'>{2}{3}</td><td width=\'30\'></td></tr></table></div>').format(orderDiscount.selectedFreeItem,getLabel('order.free'),ccy,orderDiscount.formattedAmount);
                        $('#order-item-contents').append(row);
                    }
                }
            }
        });

        // Add details of any cash discounts
        order.orderDiscounts.forEach(function(orderDiscount) {
            if( orderDiscount.discountType != 'DISCOUNT_FREE_ITEM' ) {
                var row = ('<div class=\'order-item-wrapper\'><table class=\'order-cash-discount\' width=\'236\'><tr valign=\'top\'><td width=\'146\'>{0}</td><td width=\'55\' align=\'right\'>-{1}{2}</td><td width=\'30\'></td></tr></table></div>').format(orderDiscount.title,ccy,orderDiscount.formattedAmount);
                $('#order-item-contents').append(row);
            }
        });

        // Add delivery charge if applicable
        if( order.deliveryCost && order.deliveryCost > 0 ) {
            var row = ('<div class=\'order-item-wrapper\'><table class=\'order-cash-discount\' width=\'236\'><tr valign=\'top\'><td width=\'146\'>' + getLabel('order.delivery-charge') + '</td><td width=\'55\' align=\'right\'>{0}{1}</td><td width=\'30\'></td></tr>').format(ccy,order.formattedDeliveryCost);
            $('#order-item-contents').append(row);
        }

        // Build total item cost
        $('#ordertotal').append('<span class=\'order-totalcost\'>{0}{1}</span>'.format(ccy,order.formattedTotalCost));

        // Show warning if restaurant is not open at given order time
        if( !order.restaurantIsOpen ) {
            var warning = ('<div class=\'delivery-warning-wrapper\'><div class=\'delivery-warning\'>{0}</div></div>').format(order.deliveryType == 'DELIVERY'? getLabel('order.restaurant-delivery-closed-warning'): getLabel('order.restaurant-collection-closed-warning'));
            $('#deliverycheck').append(warning);
         } else if( order.extraSpendNeededForDelivery && order.extraSpendNeededForDelivery > 0 ) {
            var warning = ('<div class=\'delivery-warning-wrapper\'><div class=\'delivery-warning\'>' + getLabel('order.delivery-warning') + '</div></div>' ).format(ccy,order.formattedExtraSpendNeededForDelivery);
            $('#deliverycheck').append(warning);
        } else {
            // Show checkout button if enabled
            if(order.canCheckout && config.enableCheckoutButton) {
                $('#checkoutcontainer').append(('<div id=\'checkout\'><a id=\'checkoutbutton\' class=\'checkout-button unselectable\'>{0}</a></div>').format(getLabel('button.checkout')));
                $('#checkoutbutton').click(function(){
                    checkout();
                });
            }
        }
    } else {
        $('#ordertotal').append('<span class=\'order-totalcost\'>' + ccy + ' 0.00</span>');
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
        display += ('<div class=\'additionalitem\'>&nbsp;&nbsp;- {0}</div>').format(unescapeQuotes(additionalItem));
    });
    return ('<div>{0}</div>').format(display);
}

// Add multiple items based on the select value
function addMultipleToOrder(restaurantId, itemId, itemType, itemSubType, additionalItemArray, additionalItemLimit, additionalItemCost ) {

    // Check restaurant with callback on restaurant id
    restaurantCheck(restaurantId, function(){
        doAddToOrderCheck(restaurantId, itemId, itemType, itemSubType, additionalItemArray, additionalItemLimit, additionalItemCost, 1);
    });
}

// Edit delivery options
function deliveryEdit() {
    $.post( ctx+'/order/deliveryEdit.ajax', { orderId: currentOrder.orderId },
        function( data ) {
            if( data.success ) {
                buildDeliveryEdit(data.days, data.deliveryTimes, data.collectionTimes, data.openForDelivery, data.openForCollection);
            } else {
                alert(data.success);
            }
        }
    );
}

// Build delivery edit form
function buildDeliveryEdit(daysArray, deliveryTimesArray, collectionTimesArray, isOpenForDelivery, isOpenForCollection) {

    // Update global delivery variables
    days = daysArray;
    deliveryTimes = deliveryTimesArray;
    collectionTimes = collectionTimesArray;
    deliveryType = currentOrder.deliveryType;
    openForDelivery = isOpenForDelivery;
    openForCollection = isOpenForCollection;

    // Indicates if the current option is for delivery
    var isdelivery = deliveryType == 'DELIVERY';

    // If deliverytimes and collection times are both empty, alert an error
    var hasDeliveryTime = false;
    deliveryTimes.forEach(function(deliveryTime){
        if(deliveryTime.length > 0 ) {
            hasDeliveryTime = true;
        }
    });

    var hasCollectionTime = false;
    collectionTimes.forEach(function(collectionTime){
        if(collectionTime.length > 0 ) {
            hasCollectionTime = true;
        }
    });

    // Restaurant not open for delivery or collection in the near future
    if( !hasDeliveryTime && !hasCollectionTime ) {

        var warningText = ('<div class=\'warning-content\'>{0}</div>').format(getLabel('order.restaurant-is-not-open-warning'));
        var warningContainer = ('<div class=\'warning-wrapper\'>{0}</div>').format(warningText);

        $.fancybox.open({
            type: 'html',
            content: warningContainer,
            minHeight:0,
            modal:false,
            openEffect:'none',
            closeEffect:'none'
        });

        return;
    }

    // Initialize wrapper for delivery options
    var deliveryContainer;

    // Build delivery edit options if there are options for both delivery and collection
    if( hasDeliveryTime && hasCollectionTime ) {
        var deliveryRadio = ('<span class=\'deliveryradio\'><input type=\'radio\' id=\'radioDelivery\' name=\'deliveryType\' value=\'DELIVERY\'{0} {1}</span>').format((isdelivery?' CHECKED>':'>'),getLabel('order.delivery'));
        var collectionRadio = ('<span class=\'deliveryradio\'><input type=\'radio\' id=\'radioCollection\' name=\'deliveryType\' value=\'COLLECTION\'{0} {1}</span>').format((isdelivery?'>':' CHECKED>'),getLabel('order.collection'));
        deliveryContainer = ('<div class=\'delivery-options-wrapper\'>{0}{1}</div>').format(deliveryRadio,collectionRadio);
    }
    else {
        var label = (hasDeliveryTime? getLabel('order.order-for-delivery'): getLabel('order.order-for-collection'));
        deliveryContainer = ('<div class=\'delivery-options-wrapper\'><div class=\'delivery-title\'>{0}:</div></div>').format(label);
        deliveryType = (hasDeliveryTime? 'DELIVERY':'COLLECTION');
    }

    // Build selection fields for day and time based on current delivery type
    var times = (deliveryType == 'DELIVERY'? deliveryTimes: collectionTimes);
    var deliverySelectContainer = buildDeliverySelection(days,times);

    // Build save and cancel buttons
    var saveButton = '<a id=\'deliverysave\' class=\'order-button add-button unselectable\'>' + getLabel('button.update') + '</a>';
    var cancelButton = '<a id=\'deliverycancel\' class=\'order-button add-button unselectable\'>' + getLabel('button.cancel') + '</a>';
    var buttonContainer = ('<div class=\'delivery-buttons\'>{0} {1}</div>').format(saveButton,cancelButton);

    // Remove the existing delivery wrapper
    $('.delivery-wrapper').remove();

    // Add the new options in the same area
    $('.order-delivery-wrapper').append(('<div class=\'delivery-wrapper\'><div class=\'delivery-form\'>{0}{1}</div>{2}</div>').format(deliveryContainer,deliverySelectContainer,buttonContainer));

    // Add onchange events to the delivery radio buttons if they are present
    if( hasDeliveryTime && hasCollectionTime ) {
        $('#radioDelivery').change(function(){
            updateDeliveryType('DELIVERY',days,deliveryTimes);
        });
        $('#radioCollection').change(function(){
            updateDeliveryType('COLLECTION',days,collectionTimes);
        });
    }

    // Add onchange event to the day select field to repopulate available times
    $('#dayselect').change(function(){
        var selectedDay = $('#dayselect').val();
        var times = (deliveryType == 'DELIVERY'? deliveryTimes: collectionTimes);
        var timeSelect = buildDeliveryTimeSelect(selectedDay,times);
        $('#timeselect').remove();
        $('.delivery-header').append(timeSelect);
    });

    // Add onclick event to the save button
    $('#deliverysave').click(function(){
        var update = {
            orderId: currentOrder.orderId,
            deliveryType: deliveryType,
            dayIndex: $('#dayselect').val(),
            time: $('#timeselect').val()
        };
        $.post( ctx+'/order/updateOrderDelivery.ajax', { body: JSON.stringify(update) },
            function( data ) {
                if( data.success ) {
                    buildOrder(data.order);
                } else {
                    alert(data.success);
                }
            }
        );
    });

    // Add onclick event to the cancel button
    $('#deliverycancel').click(function(){
        doBuildOrder(currentOrder,getOrderPanelConfig());
    });

    // If a date and time are selected, apply selection now
    if( deliveryDayOfWeek && deliveryTimeOfDay ) {
        for( var i = 0; i < days.length; i++ ) {
            if( days[i] == deliveryDayOfWeek ) {
                $('#dayselect').val(i);
                var times = (deliveryType == 'DELIVERY'? deliveryTimes: collectionTimes);
                var timeSelect = buildDeliveryTimeSelect(i,times);
                $('#timeselect').remove();
                $('.delivery-header').append(timeSelect);
                $('#timeselect').val(deliveryTimeOfDay);
            }
        }
    }
}

// Updates the selected delivery type
function updateDeliveryType(updatedDeliveryType,days,times) {

    // Update global variable
    deliveryType = updatedDeliveryType;

    // Rebuild select day and times
    var deliverySelection = buildDeliverySelection(days,times);

    // Replace select fields
    $('.delivery-header').remove();
    $('.delivery-form').append(deliverySelection);

    // Update onchange event for dayselect
    $('#dayselect').change(function(){
        var selectedDay = $('#dayselect').val();
        var timeSelect = buildDeliveryTimeSelect(selectedDay,times);
        $('#timeselect').remove();
        $('.delivery-header').append(timeSelect);
    });
}

// Builds select fields for day and time based on delivery type
function buildDeliverySelection(days,times) {
    var deliveryTimeContainer = '<div class=\'delivery-header\'>{0} - {1}</div>';
    var deliveryDaySelect = buildDeliveryDaySelect(days,times);
    var firstAvailableDay = 0;
    for( var i = 0; i < days.length; i++ ) {
        if( i == 0 ) {
            if(( deliveryType == 'DELIVERY' && openForDelivery ) || ( deliveryType == 'COLLECTION' && openForCollection )) {
                firstAvailableDay = 0;
                break;
            }
        }
        if(times[i].length > 0) {
            firstAvailableDay = i;
            break;
        }
    }
    var deliveryTimeSelect = buildDeliveryTimeSelect(firstAvailableDay,times);
    return deliveryTimeContainer.format(deliveryDaySelect,deliveryTimeSelect);
}

// Builds an array of available days that can be selected
function buildDeliveryDaySelect(days,times) {
    var select = '<select id=\'dayselect\'>';
    for( var i = 0; i < days.length; i++ ) {
        var timeArray = times[i];

        // Special case for empty time array but restaurant is currently open
        if( i == 0 ) {
            if(( deliveryType == 'DELIVERY' && openForDelivery ) || ( deliveryType == 'COLLECTION' && openForCollection )) {
                var optionLabel = getLabel('weekday.today');
                select += ('<option value=\'{0}\'>{1}</option>').format(i,optionLabel);
                continue;
            }
        }

        if( timeArray.length > 0 || ( i == 0 && openForDelivery )) {
            var optionLabel = (i == 0? getLabel('weekday.today'): getLabel('weekday.day-of-week-' + days[i]));
            select += ('<option value=\'{0}\'>{1}</option>').format(i,optionLabel);
        }
    }
    select += '</select>';
    return select;
}

// Builds an array of times that can be selected
function buildDeliveryTimeSelect(selectedDay,times) {
    var select = '<select id=\'timeselect\'>';
    var timeArray = times[selectedDay];
    if( selectedDay == 0 ) {
        if(( deliveryType == 'DELIVERY' && openForDelivery) || (deliveryType == 'COLLECTION' && openForCollection )) {
            select += ('<option value=\'{0}\'>{1}</option>').format('ASAP',getLabel('time.asap'));
        }
    }
    timeArray.forEach(function(time){
        select += ('<option value=\'{0}\'>{0}</option>').format(time);
    });
    select += '</select>';
    return select;
}

// Confirm if order should proceed
function restaurantCheck(restaurantId, callback ) {
    if( currentOrder && currentOrder.orderItems.length > 0 && currentOrder.restaurantId != restaurantId ) {

        // Build buttons for proceed and cancel
        var addItemButton = ('<a id=\'additembutton\' class=\'order-button unselectable\'>{0}</a>').format(getLabel('button.add-item-anyway'));
        var cancelButton = ('<a id=\'cancelbutton\' class=\'order-button unselectable\'>{0}</a>').format(getLabel('button.dont-add-item'));
        var buttonContainer = ('<div class=\'additional-items-buttons\'>{0} {1}</div>').format(addItemButton,cancelButton);

        // Build body content
        var warningBody = ('<div class=\'warning-container\'>' + getLabel('order.restaurant-warning') + '</div>').format(unescapeQuotes(currentOrder.restaurant.name));

        // Build header
        var warningHeader = ('<h3>{0}</h3>').format(getLabel('order.are-you-sure'));

        // Build main container
        var warningContainer = ('<div class=\'restaurant-warning-wrapper\'>{0}{1}{2}</div>').format(warningHeader,warningBody,buttonContainer);

        // Show the dialog
        $.fancybox.open({
            type: 'html',
            content: warningContainer,
            minHeight:0,
            modal:false,
            openEffect:'none',
            closeEffect:'none'
        });

        // Add click event handlers
        $('#cancelbutton').click(function(){
            $.fancybox.close(true);
        });

        $('#additembutton').click(function(){
            $.fancybox.close(true);
            callback();
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
    var canAddToOrder = true;

    // Build checkboxes for each additional item
    var additionalItemsChoices = '<div class=\'additional-items-choices\'>';
    additionalItemArray.forEach(function(additionalItem){
        var additionalItemArray = additionalItem.split('%%%');
        var additionalItemCost = additionalItemArray[1];
        var itemDiv = ('<div class=\'additional-item\'><input type=\'checkbox\' class=\'itemcheckbox\' id=\'{0}\'/>{1}<span class=\'additionalitemcost\'>{2}</span></div>')
                .format(additionalItemArray[0],unescapeQuotes(additionalItemArray[0]),(additionalItemArray[1] != 'null'? ' (' + ccy + additionalItemArray[1] + ')': ''));
        additionalItemsChoices += itemDiv;
    });
    additionalItemsChoices += '</div>';

    // build additional items header
    var additionalItemsHeader = ('<h3>{0}</h3><div id=\'itemcountwarning\'></div>').format(getLabel('order.choose-additional'));

    // Build additional items body
    var additionalItemsBody = ('<div class=\'additional-items-body\'>{0}</div>').format(additionalItemsChoices);

    // Build buttons for save and cancel
    var addItemButton = ('<a id=\'additembutton\' class=\'order-button order-button-large unselectable\'>{0}</a>').format(getLabel('button.done'));
    var cancelButton = ('<a id=\'cancelbutton\' class=\'order-button order-button-large unselectable\'>{0}</a>').format(getLabel('button.cancel'));
    var buttonContainer = ('<div class=\'additional-items-buttons\'>{0} {1}</div>').format(addItemButton,cancelButton);

    // Build main container for additional items
    var additionalItemsContainer = ('<div class=\'additional-items-wrapper\'>{0}{1}{2}</div>').format(additionalItemsHeader,additionalItemsBody,buttonContainer);

    // Placeholder for item count warning
    html += '<div id=\'itemcountwarning\'></div>';

    $.fancybox.open({
        type: 'html',
        content: additionalItemsContainer,
        modal:false,
        openEffect:'none',
        closeEffect:'none'
    });

    // Handler for the cancel button
    $('#cancelbutton').click(function(){
        $.fancybox.close(true);
    });

    // Handler for the done button
    $('#additembutton').click(function(){
        if( canAddToOrder ) {
            doAddToOrder(restaurantId, itemId, itemType, itemSubType, selectedItems.keys(), quantity );
            $.fancybox.close(true);
        }
    });

    // Handler to maintain the selected additional items
    $('.itemcheckbox').change(function(){
        if($(this).is(':checked')) {
            selectedItems.setItem($(this).attr('id'), "");
        } else {
            selectedItems.removeItem($(this).attr('id'));
        }

        canAddToOrder = true;
        $('.itemcountwarning').remove();
        if( itemLimit > 0 && selectedItems.size() > itemLimit ) {
            canAddToOrder = false;
            $('#itemcountwarning').append(('<div class=\'itemcountwarning\'>{0}</div>').format(getLabel('order.additional-item-limit')).format(itemLimit));
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

// Check if a special offer is applicable to an order
function checkCanAddSpecialOfferToOrder(restaurantId, specialOfferId, specialOfferItemsArray ) {

    var update = ({
        orderId: currentOrder.orderId,
        specialOfferId: specialOfferId
    });

    $.post( ctx+'/order/checkSpecialOffer.ajax', { body: JSON.stringify(update) },
        function( data ) {
            if( data.success ) {
                if( data.applicable ) {
                    addSpecialOfferToOrder(restaurantId, specialOfferId, specialOfferItemsArray );
                } else {
                    showSpecialOfferWarning();
                }
            } else {
                alert(data);
            }
        }
    );
}

function showSpecialOfferWarning() {

    var warning = (currentOrder.deliveryType == 'DELIVERY'? getLabel('order.special-offer-not-available-delivery'): getLabel('order.special-offer-not-available-collection'));
    var warningText = ('<div class=\'warning-content\'>{0}</div>').format(warning);
    var warningContainer = ('<div class=\'warning-wrapper\'>{0}</div>').format(warningText);

    $.fancybox.open({
        type: 'html',
        content: warningContainer,
        minHeight:0,
        modal:false,
        openEffect:'none',
        closeEffect:'none'
    });
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

    // Check restaurant with callback to add to order
    restaurantCheck(restaurantId, function(){
        doAddSpecialOfferToOrderCheck(restaurantId, specialOfferId, specialOfferItems, 1);
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
        var specialOfferItemBody = '';
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
            specialOfferItemBody += ('<div class=\'specialofferitem\'>{0}</div>').format(specialOfferItemContent);
            specialOfferItemIndex++;
        });

        // build special offer items header
        var specialOfferItemsHeader = ('<h3>{0}</h3>').format(getLabel('order.special-offer-choices'));

        // Build special offer items body
        var specialOfferItemsBody = ('<div class=\'additional-items-body\'>{0}</div>').format(specialOfferItemBody);

        // Build buttons for save and cancel
        var addItemButton = ('<a id=\'additembutton\' class=\'order-button unselectable\'>{0}</a>').format(getLabel('button.done'));
        var cancelButton = ('<a id=\'cancelbutton\' class=\'order-button unselectable\'>{0}</a>').format(getLabel('button.cancel'));
        var buttonContainer = ('<div class=\'additional-items-buttons\'>{0} {1}</div>').format(addItemButton,cancelButton);

        // Build main container for additional items
        var specialOfferItemsContainer = ('<div class=\'additional-items-wrapper\'>{0}{1}{2}</div>').format(specialOfferItemsHeader,specialOfferItemsBody,buttonContainer);

        $.fancybox.open({
            type: 'html',
            content: specialOfferItemsContainer,
            minHeight:0,
            modal:false,
            openEffect:'none',
            closeEffect:'none'
        });

        // Handler for the cancel button
        $('#cancelbutton').click(function(){
            $.fancybox.close(true);
        });

        // Handler for the add item button
        $('#additembutton').click(function(){
            var itemChoices = [];
            for( i = 0; i < specialOfferItems.length; i++) {
                var itemSelect = $('#specialOfferItemSelect_' + i );
                if( itemSelect.length ) {
                    itemChoices.push(itemSelect.val());
                } else {
                    itemChoices.push(specialOfferItems[i].itemChoices[0].text);
                }
            }
            doAddSpecialOfferToOrder(restaurantId, specialOfferId, itemChoices, quantity);
            $.fancybox.close(true);
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
                if( data.applicable ) {
                    buildOrder(data.order);
                } else {
                    showSpecialOfferWarning();
                }
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