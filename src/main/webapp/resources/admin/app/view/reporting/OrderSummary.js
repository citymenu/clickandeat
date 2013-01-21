Ext.define('AD.view.reporting.OrderSummary' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.ordersummary',
    id:'ordersummary',
    layout:'fit',
    autoScroll:true,
    loadMask:true,
    stateful:true,
    stateId:'ordersummarygridpanel',
    title:'Completed Orders',

    initComponent: function() {

        var store = Ext.widget('acceptedorderstore');
        this.store = store;

        this.features = [{
            id: 'group',
            ftype: 'groupingsummary',
            groupHeaderTpl: 'Reporting period - {name}',
            hideGroupedHeader: true,
            enableGroupingMenu: false
        }];

        this.columns = [
            {header:'Period', dataIndex:'orderCreatedMonth',flex:.1},
            {header:'ID', dataIndex:'orderId', flex:.1},
            {header:'Restaurant Name', dataIndex:'restaurantName',flex:.1},
            {header:'Type', dataIndex:'deliveryType',flex:.1},
            {header:'Payment Id', dataIndex:'transactionId',flex:.1},
            {header:'Created', dataIndex:'orderCreatedTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1},
            {
                header:'Delivery Cost', dataIndex:'deliveryCost',renderer: Ext.util.Format.Euro,flex:.1,align:'right',
                summaryType:'sum',
                summaryRenderer: Ext.util.Format.Euro
            },{
                header:'Discount', dataIndex:'totalDiscount',renderer: Ext.util.Format.Euro,flex:.1,align:'right',
                summaryType:'sum',
                summaryRenderer: Ext.util.Format.Euro
            },{
                header:'Voucher Discount', dataIndex:'voucherDiscount',renderer: Ext.util.Format.Euro,flex:.1,align:'right',
                summaryType:'sum',
                summaryRenderer: Ext.util.Format.Euro
            },{
                header:'Total Cost', dataIndex:'totalCost',renderer: Ext.util.Format.Euro,flex:.1,align:'right',
                summaryType:'sum',
                summaryRenderer: Ext.util.Format.Euro
            },{
                header:'Restaurant Cost', dataIndex:'restaurantCost',renderer: Ext.util.Format.Euro,flex:.1,align:'right',
                summaryType:'sum',
                summaryRenderer: Ext.util.Format.Euro
            },{
                 header:'Commission', dataIndex:'commission',renderer: Ext.util.Format.Euro,flex:.1,align:'right',
                 summaryType:'sum',
                 summaryRenderer: Ext.util.Format.Euro
            }
        ];

        this.dockedItems = [{
            xtype:'toolbar',
            dock:'top',
            items:[{
               text:'Refresh',
               icon: resources + '/images/refresh.gif',
               handler:function() {
                   var store = Ext.getCmp('ordersummary').getStore();
                   store.load();
               }
            }]
        }];

        this.callParent(arguments);
    }

});