/*
 * Menu item data object
 */

Ext.define('AD.model.MenuItem', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'number', type:'number'},
        {name:'title', type:'string'},
        {name:'subtitle', type:'string'},
        {name:'description', type:'string'},
        {name:'iconClass', type:'string'},
        {name:'cost', type:'double'}
    ],
    hasMany:{
        model:'AD.model.MenuItemTypeCost',
        name:'menuItemTypeCosts'
    }
});
