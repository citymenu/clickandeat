Ext.define('AD.controller.RestaurantEdit', {
    extend: 'Ext.app.Controller',
    stores:['Restaurants'],
    models: ['Restaurant','Person'],
    views:[
    	'restaurant.Edit',
    	'restaurant.MainDetails'
    ],

	init: function() {
        this.control({
            'restaurantedit button[action=close]': {
                click:this.close
            },
        });
    },

	close: function(button) {
	    location.href = ctx + '/admin/restaurants.html';
	}

});