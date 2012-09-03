/*
 * Menu item sub type data object
 */

Ext.define('AD.model.MenuItemAdditionalItemChoice', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'name', type:'string', convert: unescapeQuotes},
        {name:'cost', type:'double'}
    ],
    belongsTo: 'AD.model.MenuItem'
});
