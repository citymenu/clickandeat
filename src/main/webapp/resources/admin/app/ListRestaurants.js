Ext.Loader.setConfig({enabled:true});

Ext.application({
    name: 'AD',
    appFolder: ctx + '/resources/admin/app',
    controllers:['RestaurantList'],
    
    launch: function() {
        Ext.create('Ext.container.Viewport',{
            layout:'border',
            items:[{
                region: 'north',
                title: 'North Panel',
                collapsible: false,
                height: 120
            },{
                region:'center',
                xtype:'restaurantlist'
            }]
        });
    }
});