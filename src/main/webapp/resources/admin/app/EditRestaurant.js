Ext.Loader.setConfig({enabled:true});

Ext.application({
    name: 'AD',
    appFolder: '/resources/admin/app',
    controllers:['RestaurantEdit'],

    launch: function() {
        Ext.create('AD.view.restaurant.Edit', {
            width:880,
            height:600,
            renderTo:'main-content'
        });
    }
});