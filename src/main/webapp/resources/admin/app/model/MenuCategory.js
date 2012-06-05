
/*
 * Menu category data object
 */

Ext.define('AD.model.MenuCategory', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'name', type:'string'},
        {name:'summary', type:'string'},
        {name:'iconClass', type:'string'},
        {name:'itemTypes', type:'auto'}
    ],
    hasMany:{
        model:'AD.model.MenuItem',
        name:'menuItems'
    }
});
