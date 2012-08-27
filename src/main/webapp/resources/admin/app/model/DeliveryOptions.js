/*
 * Delivery options data object
 */

Ext.define('AD.model.DeliveryOptions', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'deliveryOptionsSummary',type: 'string'},
        {name:'deliveryTimeMinutes', type:'number'},
        {name:'minimumOrderForFreeDelivery', type:'number'},
        {name:'allowDeliveryOrdersBelowMinimum', type:'boolean'},
        {name:'deliveryCharge', type:'number'},
        {name:'deliveryRadiusInKilometres', type:'number'},
        {
            name:'areasDeliveredTo',
            type:'auto',
            convert: arrayToString
        }
    ]
});
