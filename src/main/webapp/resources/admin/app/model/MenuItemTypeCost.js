/*
 * Menu item type cost data object
 */

Ext.define('AD.model.MenuItemTypeCost', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'type', type:'string'},
        {name:'cost', type:'number'}
    ],
    belongsTo: 'AD.model.MenuItem'
});
