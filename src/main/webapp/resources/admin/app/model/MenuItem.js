/*
 * Menu item data object
 */

Ext.define('AD.model.MenuItem', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'number', type:'number'},
        {name:'itemId', type:'string'},
        {name:'title', type:'string', convert: unescapeQuotes},
        {name:'subtitle', type:'string', convert: unescapeQuotes},
        {name:'description',type: 'string',convert: replaceLineBreaks},
        {name:'iconClass', type:'string'},
        {name:'cost', type:'double'}
    ],
    hasMany:{
        model:'AD.model.MenuItemTypeCost',
        name:'menuItemTypeCosts'
    }
});
