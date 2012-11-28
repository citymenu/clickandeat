Ext.define('AD.view.order.List' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.orderlist',
    id:'orderlist',
    layout:'fit',
    autoScroll:true,
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

    features:[{
        ftype: 'filters',
        encode: false,
        stateful: true,
        stateId: 'orderListFilters'
    }],

    initComponent: function() {

        var store = Ext.widget('orderstore');
        this.store = store;

        this.columns = [
            {header:'ID', dataIndex:'orderId', flex:.1, filterable: false},
            {header:'Status', dataIndex:'orderStatus',flex:.1,filter:{type:'list',options:['AUTO CANCELLED','AWAITING RESTAURANT','BASKET','CUSTOMER CANCELLED','RESTAURANT ACCEPTED','RESTAURANT DECLINED','SYSTEM CANCELLED']}},
            {header:'Type', dataIndex:'deliveryType',flex:.1,filter:{type:'list',options:['DELIVERY','COLLECTION']}},
            {header:'Created', dataIndex:'orderCreatedTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1, filter:{type:'date'}},
            {header:'Restaurant Name', dataIndex:'restaurantName',flex:.1, filter:{type:'string'}},
            {header:'Notification Status', dataIndex:'orderNotificationStatus',flex:.1, filter:{type:'list',options:['ANSWERED','CALL IN PROGRESS','ERROR','FAILED TO RESPOND','NO ANSWER','NO CALL MADE']}},
            {header:'Call Count', dataIndex:'orderNotificationCallCount',flex:.1, filter:{type:'numeric'}},
            {header:'Order Placed', dataIndex:'orderPlacedTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1, filter:{type:'date'}},
            {header:'Expected Delivery', dataIndex:'expectedDeliveryTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1, filter:{type:'date'}},
            {header:'Expected Collection', dataIndex:'expectedCollectionTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1, filter:{type:'date'}},
            {header:'Restaurant Actioned', dataIndex:'restaurantActionedTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1, filter:{type:'date'}},
            {header:'Confirmed Time', dataIndex:'restaurantConfirmedTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1, filter:{type:'date'}},
            {header:'Voucher Id', dataIndex:'voucherId',flex:.1, filter:{type:'string'}},
            {header:'Delivery Cost', dataIndex:'deliveryCost',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1, filter:{type:'numeric'}},
            {header:'Discount', dataIndex:'totalDiscount',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1, filter:{type:'numeric'}},
            {header:'Voucher Discount', dataIndex:'voucherDiscount',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1, filter:{type:'numeric'}},
            {header:'Total Cost', dataIndex:'totalCost',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1, filter:{type:'numeric'}},
            {header:'Restaurant Cost', dataIndex:'restaurantCost',renderer: Ext.util.Format.numberRenderer('0.00'),flex:.1, filter:{type:'numeric'}},
            {header:'Transaction Id', dataIndex:'transactionId',flex:.1, filter:{type:'string'}},
            {header:'Transaction Status', dataIndex:'transactionStatus',flex:.1,filter:{type:'list',options:['CAPTURED','ERROR','PREAUTHORISED','REFUNDED']}}
        ];

        this.dockedItems = [{
            xtype:'toolbar',
            dock:'top',
            items:[{
               text:'Refresh',
               icon: resources + '/images/refresh.gif',
               handler:function() {
                   var store = Ext.getCmp('orderlist').getStore();
                   store.loadPage(store.currentPage);
               }
            },'-',{
                text:'Clear Filters',
                icon: resources + '/images/icons-shadowless/cross.png',
                handler:function() {
                    Ext.getCmp('orderlist').filters.clearFilters();
                }
            },'-',{
                text:'Download to Excel',
                icon:'../resources/images/icons-shadowless/report-excel.png',
                handler:function(){
                    ctrl.downloadToExcel();
                }
            },'->',{
                width:300,
                fieldLabel:'Search by OrderId',
                labelWidth:100,
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