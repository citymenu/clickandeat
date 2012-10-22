/*
 * Address data object
 */

Ext.define('AD.model.Address', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'address1', type:'string',convert: unescapeQuotes},
        {name:'town', type:'string',convert: unescapeQuotes},
        {name:'region', type:'string',convert: unescapeQuotes},
        {name:'postCode', type:'string',convert: unescapeQuotes}
    ]
});

