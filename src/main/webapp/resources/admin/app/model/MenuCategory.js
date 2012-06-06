
/*
 * Menu category data object
 */

Ext.define('AD.model.MenuCategory', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'name', type:'string'},
        {name:'type', type:'string'},
        {name:'summary', type:'string'},
        {name:'iconClass', type:'string'},
        {
            name:'itemTypes',
            type:'auto',
            convert: function(value, record) {
                if( value && (value instanceof Array)) {
                    return value.join('\n');
                } else {
                    return value;
                }
            }
        }
    ],
    hasMany:{
        model:'AD.model.MenuItem',
        name:'menuItems'
    }
});
