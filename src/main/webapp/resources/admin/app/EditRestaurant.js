Ext.Loader.setConfig({enabled:true});

// Load the restaurant object on startup
Ext.onReady(function(){
    Ext.Ajax.request({
        url: ctx + (restaurantId && restaurantId != '')? '/admin/restaurants/load.ajax': '/admin/restaurants/create.ajax',
        method:'POST',
        params: { restaurantId: restaurantId },
        success: function(response) {
            var obj = Ext.decode(response.responseText);
            restaurantObj = JSON.parse(obj.restaurant);
            onRestaurantLoaded();
        }
    });
});

// Builds the application once the restaurant object is loaded
function onRestaurantLoaded() {
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
}
