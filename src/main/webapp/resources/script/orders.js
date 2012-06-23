
$(document).ready(function(){
    buildOrder(order);
});

// Build the order display component
function buildOrder(order) {
    $('.orderitemrow').remove();
    $('.totalitemcost').remove();
    if( order ) {
        order.orderItems.forEach(function(orderItem) {
            var row = '<tr class=\'orderitemrow\' valign=\'top\'><td>{0}</td><td align=\'center\'>{1}</td><td align=\'right\'>{2}</td><td align=\'center\'><a href=\'#\' onclick=\"removeFromOrder(\'{3}\')\">Remove</a></td></tr>'
                .format(orderItem.menuItemTitle,orderItem.quantity,orderItem.cost * orderItem.quantity,orderItem.menuItemId);
            $('.orderbody').append(row);
        });
        $('.totalcost').append('<span class=\'totalitemcost\'>{0}</span>'.format(order.orderItemCost));
    } else {
        $('.totalcost').append('<span class=\'totalitemcost\'>0.00</span>');
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