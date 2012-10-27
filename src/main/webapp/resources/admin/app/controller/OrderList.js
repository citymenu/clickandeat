Ext.define('AD.controller.OrderList', {
    extend: 'Ext.app.Controller',
    stores:['Orders'],
    models: ['Order'],
    views:['order.List'],

    refs: [{
        ref:'orderList',
        selector:'orderlist'
    }],

	init: function() {
		this.control({
		    'orderlist': {
                render:this.onGridRendered,
                itemdblclick:this.onGridDblClick
            }
		});
	},

	onGridRendered: function(grid) {
		grid.getStore().loadPage(1);
	},

	getSelectedRecord: function() {
	    return this.getOrderList().getSelectionModel().getLastSelected();
	},

	updateNavigation:function(div) {
	}


});