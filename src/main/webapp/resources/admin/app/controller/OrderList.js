Ext.define('AD.controller.OrderList', {
    extend: 'Ext.app.Controller',
    stores:['Orders'],
    models: ['Order'],
    views:['order.List','order.OrderAmendment'],

    refs: [{
        ref:'orderList',
        selector:'orderlist'
    }],

	init: function() {
		this.control({
		    'orderlist': {
                render:this.onGridRendered,
                itemdblclick:this.onGridDblClick,
                itemcontextmenu:this.onContextMenu
            }
		});
	},

	onGridRendered: function(grid) {
		grid.getStore().loadPage(1);
	},

	getSelectedRecord: function() {
	    return this.getOrderList().getSelectionModel().getLastSelected();
	},

	onContextMenu: function(view, record, item, index, e ) {
	    e.stopEvent();
	    this.getOrderList().getSelectionModel().select(record);
	    var ctrl = this;
	    var menu = Ext.create('Ext.menu.Menu',{
	        id:'order-menu',
            items:[{
                text:'Cancel order',
                icon:'../resources/images/icons-shadowless/cross.png',
                handler:function(){
                    ctrl.cancelOrder();
                }
            },{
                text:'Amend order',
                icon:'../resources/images/icons-shadowless/document-table.png',
                handler:function(){
                    ctrl.amendOrder();
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

	cancelOrder:function() {
	    var order = this.getSelectedRecord();
        Ext.MessageBox.show({
            title:'Cancel Order',
            msg:'Are you sure you want to cancel this order?',
            buttons:Ext.MessageBox.YESNO,
            icon:Ext.MessageBox.QUESTION,
            closable:false,
            fn:function(result) {
                if(result == 'yes') {
                    $.fancybox.showLoading();
                    Ext.Ajax.request({
                        url: ctx + '/admin/orders/cancel.ajax',
                        method:'POST',
                        params: {
                            orderId: order.get('orderId')
                        },
                        success: function(response) {
                            $.fancybox.hideLoading();
                            var obj = Ext.decode(response.responseText);
                            if( obj.success ) {
                                showSuccessMessage(Ext.get('orderlist'),'Cancelled','Order has been cancelled');
                                var store = Ext.getCmp('orderlist').getStore();
                                store.loadPage(store.currentPage);
                            } else {
                                showErrorMessage(Ext.get('orderlist'),'Error',obj.message);
                            }
                        },
                        failure: function(response) {
                            $.fancybox.hideLoading();
                            var obj = Ext.decode(response.responseText);
                            showErrorMessage(Ext.get('orderlist'),'Error',obj.message);
                        }
                    });
                }
            }
        });
	},

	amendOrder:function() {
        var order = this.getSelectedRecord();
        var win = Ext.create('widget.window', {
            title:'Amend Order',
            id:'amendmentwindow',
            closable: true,
            closeAction:'destroy',
            width: 600,
            autoHeight:true,
            modal:true,

            dockedItems:[{
                xtype:'toolbar',
                dock:'top',
                items:[{
                    xtype:'button',
                    text:'Save Amendment',
                    handler:function() {
                        var formPanel = Ext.getCmp('orderamendment');
                        var form = formPanel.getForm();
                        if( form.isValid()) {
                            formPanel.submit({
                                url:ctx + '/admin/orders/addOrderAmendment.ajax',
                                success: function(form,action) {
                                    var obj = Ext.decode(action.response.responseText);
                                    if( obj.success ) {
                                        showSuccessMessage(Ext.get('orderlist'),'Amended','Order has been amended');
                                        var store = Ext.getCmp('orderlist').getStore();
                                        store.loadPage(store.currentPage);
                                        Ext.getCmp('amendmentwindow').close();
                                    } else {
                                        showErrorMessage(Ext.get('orderlist'),'Error',obj.message);
                                    }
                                },
                                failure: function(form,action) {
                                    var obj = Ext.decode(action.response.responseText);
                                    showErrorMessage(Ext.get('orderlist'),'Error',obj.message);
                                }
                            });
                        }
                    }
                }]
            }],
            layout: {
                type: 'fit',
            },
            items: [{
                xtype:'orderamendmentedit'
            }]
        });
        win.show();
        win.down('form').getForm().findField('orderId').setValue(order.get('orderId'));
        win.down('form').getForm().findField('restaurantCost').setValue(order.get('restaurantCost'));
        win.down('form').getForm().findField('totalCost').setValue(order.get('totalCost'));
	}

});