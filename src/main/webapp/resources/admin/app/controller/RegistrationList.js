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
                render:this.onGridRendered,
                itemcontextmenu:this.onContextMenu
            }
		});
	},

	onGridRendered: function(grid) {
		grid.getStore().loadPage(1);
	},

	getSelectedRecord: function() {
	    return this.getRegistrationList().getSelectionModel().getLastSelected();
	},

	onContextMenu: function(view, record, item, index, e ) {
	    e.stopEvent();
	    this.getRegistrationList().getSelectionModel().select(record);
	    var ctrl = this;
	    var menu = Ext.create('Ext.menu.Menu',{
	        id:'order-menu',
            items:[{
                text:'Generate email',
                icon:'../resources/images/icons-shadowless/mail.png',
                handler:function(){
                    ctrl.generateEmail();
                }
            },'-',{
                text:'Mark email sent',
                icon:'../resources/images/icons-shadowless/tick-circle.png',
                handler:function(){
                    ctrl.markEmailSent();
                }
            }],
            listeners:{
                'hide': {
                    fn:function(){
                        this.destroy();
                    }
                }
            }
	    });
	    menu.showAt(e.getXY());
	},

	generateEmail:function() {
	    var registration = this.getSelectedRecord();
        Ext.Ajax.request({
            url: ctx + '/admin/registrations/generateEmail.ajax',
            method:'POST',
            params: {
                registrationId: registration.get('id')
            },
            success: function(response) {
                var obj = Ext.decode(response.responseText);
                if( obj.success ) {
                    var email = obj.email;
                    var voucher = obj.voucher;
                    var discount = obj.discount;
                    window.location = 'mailto:' + email + '?subject=Your discount coupon for ' + discount + '% off your next online order&body=' + voucher;
                } else {
                    showErrorMessage(Ext.get('registrationlist'),'Error',obj.message);
                }
            },
            failure: function(response) {
                var obj = Ext.decode(response.responseText);
                showErrorMessage(Ext.get('registrationlist'),'Error',obj.message);
            },
            scope:this
        });
	},

	markEmailSent:function() {
	    var registration = this.getSelectedRecord();
        Ext.Ajax.request({
            url: ctx + '/admin/registrations/markEmailSent.ajax',
            method:'POST',
            params: {
                registrationId: registration.get('id')
            },
            success: function(response) {
                var obj = Ext.decode(response.responseText);
                if( obj.success ) {
                    showSuccessMessage(Ext.get('registrationlist'),'Updated','Email marked as sent');
                    var store = Ext.getCmp('registrationlist').getStore();
                    store.loadPage(store.currentPage);
                } else {
                    showErrorMessage(Ext.get('registrationlist'),'Error',obj.message);
                }
            },
            failure: function(response) {
                var obj = Ext.decode(response.responseText);
                showErrorMessage(Ext.get('registrationlist'),'Error',obj.message);
            },
            scope:this
        });
	}

});