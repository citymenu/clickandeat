Ext.Loader.setConfig({enabled:true});

Ext.application({
    name: 'AD',
    appFolder: '/resources/admin/app',
    controllers:['RestaurantList'],
    
    launch: function() {
        Ext.create('AD.view.restaurant.List', {
            width:880,
            height:600,
            renderTo:'main-content'
        });
    }
});