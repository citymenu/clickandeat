
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
            alert('success:' + data.success);
        }
    );

}