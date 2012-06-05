Ext.Loader.setConfig({enabled:true});

Ext.application({
    name: 'AD',
    appFolder: '/resources/admin/app',
    controllers:['RestaurantList'],
    
    launch: function() {
        Ext.create('AD.view.restaurant.List', {
            width:1080,
            height:650,
            renderTo:'main-content'
        });
    }
});