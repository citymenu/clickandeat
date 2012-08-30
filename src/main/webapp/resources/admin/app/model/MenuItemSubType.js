/*
 * Menu item sub type data object
 */

Ext.define('AD.model.MenuItemSubType', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'type', type:'string', convert: unescapeQuotes},
        {name:'cost', type:'double'}
    ],
    belongsTo: 'AD.model.MenuItem'
});
