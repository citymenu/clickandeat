/*
 * Special offer data object
 */

Ext.define('AD.model.SpecialOffer', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'specialOfferId', type:'string'},
        {name:'number', type:'int'},
        {name:'title', type:'string', convert: unescapeQuotes},
        {name:'description',type: 'string',convert: unescapeQuotes},
        {name:'cost', type:'double'}
    ],
    hasMany:{
        model:'AD.model.SpecialOfferItem',
        name:'specialOfferItems'
    },
    hasMany:{
        model:'AD.model.SpecialOfferApplicableTime',
        name:'offerApplicableTimes'
    }
});
