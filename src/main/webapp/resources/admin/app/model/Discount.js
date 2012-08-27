/*
 * Discount data object
 */

Ext.define('AD.model.Discount', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'discountId', type:'string'},
        {name:'title', type:'string', convert: unescapeQuotes},
        {name:'description',type: 'string',convert: unescapeQuotes},
        {name:'discountType', type:'string'},
        {name:'collection', type:'boolean'},
        {name:'delivery', type:'boolean'},
        {name:'delivery', type:'canCombineWithOtherDiscounts'},
        {name:'minimumOrderValue', type:'double'},
        {name:'discountAmount', type:'double'},
        {name:'freeItems',type:'auto',convert: arrayToString}
    ],
    hasMany:{
        model:'AD.model.DiscountApplicableTime',
        name:'discountApplicableTimes'
    }
});
