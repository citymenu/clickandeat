Ext.define('AD.view.order.List' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.orderlist',
    id:'orderlist',
    layout:'fit',
    loadMask:true,

    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl : [
            '<tpl for=".">',
                '<div class="order-summary">',
                    '<div class="order-updates">',
                        '<h2>Order activity</h2>',
                        '<tpl for="orderUpdates">',
                            '<div class="{[xindex % 2 === 0 ? "order-update even" : "order-update odd"]}">',
                                '{updateTime} - <b>{text}</b>',
                            '</div>',
                        '</tpl>',
                    '</div>',
                '</div>',
                '<tpl if="orderAmendments.length &gt; 0">',
                    '<div class="order-summary">',
                        '<div class="order-updates">',
                            '<h2>Order amendments</h2>',
                            '<tpl for="orderAmendments">',
                                '<div class="{[xindex % 2 === 0 ? "order-update even" : "order-update odd"]}">',
                                    '<div class="order-update-element">{created}&nbsp;-&nbsp;</div>',
                                    '<div class="order-update-element">',
                                        '<b>{description}</b><br>',
                                        'Restaurant cost updated from &#128;{previousRestaurantCost:number("0.00")} to &#128;{restaurantCost:number("0.00")}<br>',
                                        'Total cost updated from &#128;{previousTotalCost:number("0.00")} to &#128;{totalCost:number("0.00")}',
                                    '</div>',
                                '</div>',
                            '</tpl>',
                        '</div>',
                    '</div>',
                '</tpl>',
            '</tpl>'
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
            {header:'Restaurant Cost', dataIndex:'restaurantCost',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1},
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