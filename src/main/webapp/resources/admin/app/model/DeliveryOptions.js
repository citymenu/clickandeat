/*
 * Delivery options data object
 */

Ext.define('AD.model.DeliveryOptions', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'deliveryOptionsSummary',type: 'string',convert: unescapeQuotes},
        {name:'deliveryTimeMinutes', type:'number'},
        {name:'collectionTimeMinutes', type:'number'},
        {name:'minimumOrderForDelivery', type:'number'},
        {name:'deliveryCharge', type:'number'},
        {name:'collectionOnly', type:'boolean'},
        {name:'allowFreeDelivery', type:'boolean'},
        {name:'minimumOrderForFreeDelivery', type:'number'},
        {name:'allowDeliveryBelowMinimumForFreeDelivery', type:'boolean'},
        {name:'deliveryRadiusInKilometres', type:'number'},
        {
            name:'areasDeliveredTo',
            type:'auto',
            convert: arrayToString
        }
    ]
});

