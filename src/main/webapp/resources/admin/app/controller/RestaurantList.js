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
        location.href = ctx + '/admin/restaurants/edit.html';
    },

    editSelected: function() {
        var record = this.getSelectedRecord();
        if( record ) {
            var restaurantId = record.get('restaurantId');
            location.href = ctx + '/admin/restaurants/edit.html?restaurantId=' + restaurantId;
        }
    },

    deleteSelected: function(button) {
        var record = this.getSelectedRecord();
        if( record ) {
            Ext.Ajax.request({
                url: ctx + '/admin/restaurants/delete.ajax',
                method:'GET',
                params: {
                    restaurantId: record.get('restaurantId')
                },
                success: function(response) {
                    var obj = Ext.decode(response.responseText);
                    if( obj.success ) {
                        showSuccessMessage(Ext.get('restaurantlist'),'Deleted','Restaurant deleted successfully');
                        this.getRestaurantsStore().loadPage(1);
                    } else {
                        showErrorMessage(Ext.get('restaurantlist'),'Error',obj.message);
                    }
                },
                failure: function(response) {
                    var obj = Ext.decode(response.responseText);
                    showErrorMessage(Ext.get('restaurantlist'),'Error',obj.message);
                },
                scope:this
            });
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