
$(document).ready(function(){
    buildOrder(order);
});

// Build the order display component
function buildOrder(order) {
    $('.orderitemrow').remove();
    $('.totalitemcost').remove();
    $('.checkoutbutton').remove();
    if( order ) {
        for (var i = order.orderItems.length - 1; i >= 0; i--) {
            var orderItem = order.orderItems[i];
            var row = '<tr class=\'orderitemrow\' valign=\'top\'><td>{0}</td><td align=\'center\'>{1}</td><td align=\'right\'>{2}{3}</td><td align=\'center\'><a href=\'#\' onclick=\"removeFromOrder(\'{4}\')\">Remove</a></td></tr>'
                .format(orderItem.menuItemTitle,orderItem.quantity,ccy,(orderItem.cost * orderItem.quantity).toFixed(2),orderItem.menuItemId);
            $('.orderbody').prepend(row);
        };
        $('.totalcost').append('<span class=\'totalitemcost\'>{0}{1}</span>'.format(ccy,order.orderItemCost.toFixed(2)));
        if( order.orderItems.length > 0 ) {
            $('.checkout').append('<input type=\'button\' value=\'Checkout\' class=\'checkoutbutton\'>');
            $('.checkoutbutton').button();
        }
    } else {
        $('.totalcost').append('<span class=\'totalitemcost\'>' + ccy + '0.00</span>');
    }
}

// Add item to order update result on display
function addToOrder(restaurantId, itemId, itemName, itemCost, quantity ) {

    var update = new Object({
        restaurantId: restaurantId,
        itemId: itemId,
        itemName: itemName,
        itemCost: itemCost,
        quantity: quantity || 1
    });

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

    var update = new Object({
        itemId: itemId,
        quantity: quantity || 1
    });

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