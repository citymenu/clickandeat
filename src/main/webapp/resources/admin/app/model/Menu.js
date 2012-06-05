/*
 * Menu data object
 */

Ext.define('AD.model.Menu', {
    extend: 'Ext.data.Model',
    hasMany:{
        model:'AD.model.MenuCategory',
        name:'menuCategories'
    }
});
