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
            items:[
            {
                text:'Send voucher email',
                icon:'../resources/images/icons-shadowless/mail.png',
                handler:function(){
                    ctrl.sendVoucherEmail();
                }
            }
            ],
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

	sendVoucherEmail:function() {
    	    var registration = this.getSelectedRecord();

    	    if(registration.get('emailSent')){
                showErrorMessage(Ext.get('registrationlist'),'Error', "A voucher has already been sent to this customer");
    	    }else{
                Ext.Ajax.request({
                    url: ctx + '/admin/registrations/generateEmail2.ajax',
                    method:'POST',
                    params: {
                        registrationId: registration.get('id')
                    },
                    success: function(response) {
                        var obj = Ext.decode(response.responseText);
                        if( obj.success ) {
                            showSuccessMessage(Ext.get('registrationlist'),'Updated','Email with voucher sent to customer');
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

    }

});