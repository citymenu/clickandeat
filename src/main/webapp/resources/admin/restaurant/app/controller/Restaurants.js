Ext.define('AD.controller.Restaurants', {
    extend: 'Ext.app.Controller',
    stores:['Restaurants'],
    models: ['Restaurant'],
    views:[
    	'restaurant.List'
    ],
    
	init: function() {
		this.control({
		    'restaurantlist': {
                render:this.onGridRendered
            },

            'restaurantlist button[action=refresh]': {
            	click:this.refresh
            },
	
            'restaurantlist button[action=create]': {
            	click:this.create
            },

            'restaurantlist button[action=edit]': {
            	click:this.editSelected
            },

            'restaurantlist button[action=delete]': {
            	click:this.deleteSelected
            }
		});
	},

    refresh: function(button) {
    	reloadListStore('restaurantlist');
    },

	onGridRendered: function(grid) {
		this.getRestaurantsStore().loadPage(1);
	}
    
});