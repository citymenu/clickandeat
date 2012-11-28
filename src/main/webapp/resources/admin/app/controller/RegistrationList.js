Ext.define('AD.controller.RegistrationList', {
    extend: 'Ext.app.Controller',
    stores:['Registrations'],
    models: ['Registration','Order','GeoLocation'],
    views:['registration.List'],

    refs: [{
        ref:'registrationList',
        selector:'registrationlist'
    }],

	init: function() {
		this.control({
		    'registrationlist': {
                render:this.onGridRendered
            }
		});
	},

	onGridRendered: function(grid) {
		grid.getStore().loadPage(1);
	},

	getSelectedRecord: function() {
	    return this.getOrderList().getSelectionModel().getLastSelected();
	}

});