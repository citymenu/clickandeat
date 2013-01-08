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
                contentEl: 'north',
                collapsible: false,
                height: 103,
                frame: false,
                border:false
            },{
                region:'center',
                xtype:'restaurantlist',
                frame:true
            }]
        });
    }
});