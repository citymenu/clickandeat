Ext.define('AD.controller.Reporting', {
    extend: 'Ext.app.Controller',
    stores:['Orders'],
    models: ['Order'],
    views:['reporting.TabPanel','reporting.OrderSummary'],

    refs: [{
        ref:'orderSummary',
        selector:'ordersummary'
    }],

	init: function() {
		this.control({
		    'ordersummary': {
                render:this.onGridRendered,
            }
		});
	},

	onGridRendered: function(grid) {
		grid.getStore().loadPage(1);
	}

});