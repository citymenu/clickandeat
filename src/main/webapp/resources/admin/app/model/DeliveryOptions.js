/*
 * Delivery options data object
 */

Ext.define('AD.model.DeliveryOptions', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'deliveryOptionsSummary', type:'string'},
        {name:'minimumOrderForFreeDelivery', type:'number'},
        {name:'deliveryCharge', type:'number'},
        {name:'collectionDiscount', type:'number'},
        {name:'minimumOrderForCollectionDiscount', type:'number'},
        {name:'deliveryRadiusInKilometres', type:'number'},
        {
            name:'areasDeliveredTo',
            type:'auto',
            convert: function(value, record) {
                return value.join('\n');
            }
        }
    ]
});
