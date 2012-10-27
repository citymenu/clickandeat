/*
 * Order data object
 */

Ext.define('AD.model.Order', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'orderId', type:'string'},
        {name:'deliveryType', type:'string'},
        {name:'orderStatus', type:'string',convert:convertOrderStatus},
        {name:'restaurantId', type:'string'},
        {name:'restaurantName', type:'string',convert: unescapeQuotes},
        {name:'voucherId', type:'string'},
        {name:'totalCost', type:'number'},
        {name:'deliveryCost', type:'number'},
        {name:'totalDiscount', type:'number'},
        {name:'voucherDiscount', type:'number'},
        {name:'orderPlacedTime', type:'date'},
        {name:'expectedDeliveryTime', type:'date'},
        {name:'expectedCollectionTime', type:'date'},
        {name:'restaurantActionedTime', type:'date'},
        {name:'restaurantConfirmedTime', type:'date'},
        {name:'transactionId', type:'string'},
        {name:'transactionStatus', type:'string'}
    ]
});

