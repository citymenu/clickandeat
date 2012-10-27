Ext.define('AD.view.order.List' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.orderlist',
    id:'orderlist',
    layout:'fit',
    loadMask:true,

    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl : [
            '<p><b>Order id:</b> {orderId}</p>'
        ]
    }],

    initComponent: function() {

        var store = Ext.widget('orderstore');
        this.store = store;

        this.columns = [
            {header:'ID', dataIndex:'orderId',flex:.1},
            {header:'Status', dataIndex:'orderStatus',flex:.1},
            {header:'Type', dataIndex:'deliveryType',flex:.1},
            {header:'Restaurant Name', dataIndex:'restaurantName',flex:.1},
            {header:'Order Placed', dataIndex:'orderPlacedTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1},
            {header:'Expected Delivery', dataIndex:'expectedDeliveryTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1},
            {header:'Expected Collection', dataIndex:'expectedCollectionTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1},
            {header:'Restaurant Actioned', dataIndex:'restaurantActionedTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1},
            {header:'Confirmed Time', dataIndex:'restaurantConfirmedTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1},
            {header:'Voucher Id', dataIndex:'voucherId',flex:.1},
            {header:'Delivery Cost', dataIndex:'deliveryCost',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1},
            {header:'Discount', dataIndex:'totalDiscount',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1},
            {header:'Voucher Discount', dataIndex:'voucherDiscount',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1},
            {header:'Total Cost', dataIndex:'totalCost',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1},
            {header:'Transaction Id', dataIndex:'transactionId',flex:.1},
            {header:'Transaction Status', dataIndex:'transactionStatus',flex:.1}

        ];

        this.dockedItems = [{
            xtype:'toolbar',
            dock:'top',
            items:[{
                width:250,
                fieldLabel:'Search',
                labelWidth:50,
                xtype:'searchfield',
                store:store
            }]
        },{
            xtype:'pagingtoolbar',
            store:store,
            dock:'bottom',
            displayInfo: true
        }];

        this.callParent(arguments);
    },

    getSelectedRecord: function() {
    	if(this.getSelectionModel().hasSelection()) {
    		return this.getSelectionModel().getLastSelected();
    	}
    	else {
    		return null;
    	}
    }
});