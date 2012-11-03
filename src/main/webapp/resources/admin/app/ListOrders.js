Ext.Loader.setConfig({enabled:true});
Ext.Loader.setPath('Ext.ux', '../resources/ext/src/ux');

Ext.require([
    'Ext.ux.RowExpander',
    'Ext.ux.form.SearchField',
    'Ext.ux.grid.FiltersFeature'
]);

Ext.application({
    name: 'AD',
    appFolder: ctx + '/resources/admin/app',
    controllers:['OrderList'],
    
    launch: function() {
        Ext.create('Ext.container.Viewport',{
            layout:'border',
            items:[{
                region: 'north',
                contentEl: 'north',
                collapsible: false,
                height: 133,
                frame: false,
                border:false
            },{
                region:'center',
                xtype:'orderlist',
                frame:true
            }]
        });
    }
});