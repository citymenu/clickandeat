/*
 * Applicable time for discount
 */

Ext.define('AD.model.DiscountApplicableTime', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'dayOfWeek', type:'int'},
        {name:'applicable', type:'boolean'},
        {name:'applicableFrom', type:'string'},
        {name:'applicableTo', type:'string'}
    ],
    belongsTo: 'AD.model.Discount'
});
