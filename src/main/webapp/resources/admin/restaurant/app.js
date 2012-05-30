Ext.Loader.setConfig({enabled:true});

Ext.application({
    name: 'AD',
    appFolder: '/resources/admin/restaurant/app',
    controllers:['Restaurants'],
    
    launch: function() {
        Ext.create('AD.view.restaurant.List', {
            width:1080,
            height:550,
            renderTo:'main-content'
        });
    }
});