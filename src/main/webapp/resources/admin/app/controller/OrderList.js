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
	}

});