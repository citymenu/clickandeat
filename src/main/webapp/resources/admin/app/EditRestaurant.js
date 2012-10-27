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
                items:[{
                    region: 'north',
                    contentEl: 'north',
                    collapsible: false,
                    height: 133,
                    frame: false,
                    border:false
                },{
                    region:'center',
                    xtype:'restaurantedit',
                    frame:true
                }]
            });
        }
    });
}
