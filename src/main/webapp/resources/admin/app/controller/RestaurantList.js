Ext.define('AD.controller.RestaurantList', {
    extend: 'Ext.app.Controller',
    stores:['Restaurants'],
    models: ['Restaurant'],
    views:[
    	'restaurant.List'
    ],

    refs: [{
        ref:'restaurantList',
        selector:'restaurantlist'
    }],

	init: function() {
		this.control({
		    'restaurantlist': {
                render:this.onGridRendered,
                itemdblclick:this.onGridDblClick
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

    create: function(button) {
        location.href = ctx + '/admin/restaurants/create.html';
    },

    editSelected: function() {
        var record = this.getSelectedRecord();
        if( record ) {
            var restaurantId = record.get('restaurantId');
            location.href = ctx + '/admin/restaurants/edit.html?restaurantId=' + restaurantId;
        }
    },

	onGridRendered: function(grid) {
		this.getRestaurantsStore().loadPage(1);
	},

	onGridDblClick: function(view,record,item,index,eventObj,options) {
        var restaurantId = record.get('restaurantId');
        location.href = ctx + '/admin/restaurants/edit.html?restaurantId=' + restaurantId;
	},

	getSelectedRecord: function() {
	    return this.getRestaurantList().getSelectionModel().getLastSelected();
	}

});