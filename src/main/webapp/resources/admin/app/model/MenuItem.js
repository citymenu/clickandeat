/*
 * Menu item data object
 */

Ext.define('AD.model.MenuItem', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'type', type:'string'},
        {name:'number', type:'number'},
        {name:'itemId', type:'string'},
        {name:'title', type:'string', convert: unescapeQuotes},
        {name:'subtitle', type:'string', convert: unescapeQuotes},
        {name:'description',type: 'string',convert: unescapeQuotes},
        {name:'iconClass', type:'string'},
        {name:'cost', type:'double'},
        {name:'additionalItemCost', type:'double'},
        {name:'additionalItemChoiceLimit', type:'double'}
    ],
    hasMany:{
        model:'AD.model.MenuItemTypeCost',
        name:'menuItemTypeCosts'
    },
    hasMany:{
        model:'AD.model.MenuItemSubType',
        name:'menuItemSubTypes'
    },
    hasMany:{
        model:'AD.model.MenuItemAdditionalItemChoice',
        name:'additionalItemChoices'
    }
});
