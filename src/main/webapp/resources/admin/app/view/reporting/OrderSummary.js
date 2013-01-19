Ext.define('AD.view.reporting.OrderSummary' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.ordersummary',
    id:'ordersummary',
    layout:'fit',
    autoScroll:true,
    loadMask:true,
    stateful:true,
    stateId:'ordersummarygridpanel',
    title:'Order Summary',

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
            }]
        }];

        this.callParent(arguments);
    }

});