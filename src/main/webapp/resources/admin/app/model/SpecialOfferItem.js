/*
 * Special offer item data object
 */

Ext.define('AD.model.SpecialOfferItem', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'title', type:'string', convert: unescapeQuotes},
        {name:'description',type: 'string',convert: unescapeQuotes},
        {name:'specialOfferItemChoices',type:'auto',convert: arrayToString}
    ],
    belongsTo: 'AD.model.SpecialOffer'
});
