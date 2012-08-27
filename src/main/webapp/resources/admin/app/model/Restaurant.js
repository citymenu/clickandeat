/*
 * Restaurant data object
 */

Ext.define('AD.model.Restaurant', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'restaurantId', type:'string'},
        {name:'name', type:'string',convert: unescapeQuotes},
        {name:'description',type: 'string'},
        {name:'contactEmail', type:'string'},
        {name:'contactTelephone', type:'string'},
        {name:'contactMobile', type:'string'},
        {name:'website', type:'string'},
        {name:'cuisines', type:'auto'},
        {name:'imageId', type:'string'}
    ]
});

