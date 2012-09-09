/*
 * Applicable time for special offer
 */

Ext.define('AD.model.SpecialOfferApplicableTime', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'dayOfWeek', type:'int'},
        {name:'applicable', type:'boolean'},
        {name:'applicableFrom', type:'string'},
        {name:'applicableTo', type:'string'}
    ],
    belongsTo: 'AD.model.SpecialOffer'
});
