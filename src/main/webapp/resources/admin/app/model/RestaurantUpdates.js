/*
 * Restaurant updates data object
 */

Ext.define('AD.model.RestaurantUpdates', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'text', type:'string', convert: unescapeQuotes},
        {name:'updateTime', type:'date'}
    ]
});
