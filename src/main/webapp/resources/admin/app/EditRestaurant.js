Ext.Loader.setConfig({enabled:true});

Ext.application({
    name: 'AD',
    appFolder: ctx + '/resources/admin/app',
    controllers:['RestaurantEdit'],

    launch: function() {
        Ext.create('Ext.container.Viewport',{
            layout:'border',
            layout:'border',
            items:[{
                region: 'north',
                title: 'North Panel',
                collapsible: false,
                height: 120
            },{
                region: 'west',
                title: 'West Panel',
                collapsible: false,
                split: true,
                width: 225
            },{
                region:'center',
                xtype:'restaurantedit'
            },{
                region: 'south',
                title: 'South Panel',
                collapsible: false,
                height: 80
            }]
        });
    }
});