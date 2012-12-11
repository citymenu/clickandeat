Ext.define('AD.view.registration.List' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.registrationlist',
    id:'registrationlist',
    layout:'fit',
    autoScroll:true,
    loadMask:true,

    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl : [
            '<tpl for=".">',
                '<tpl if="location != null">',
                    '<div class="order-summary">',
                        '<div class="order-updates">',
                            '<h2>Address</h2>',
                            '<tpl for="location">',
                                '<div class="order-update odd">{displayAddress}</div>',
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
        stateId: 'registrationListFilters'
    }],

    initComponent: function() {

        var store = Ext.widget('registrationstore');
        this.store = store;

        this.columns = [
            {header:'Email address', dataIndex:'emailAddress', flex:.1,filter:{type:'string'}},
            {header:'Created', dataIndex:'created',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1,filter:{type:'date'}},
            {header:'Discount', dataIndex:'requestedDiscount',flex:.1, filter:{type:'numeric'}},
            {header:'Order Id', dataIndex:'order.orderId', renderer: renderOrderId, type:'string',flex:.1,filterable:false},
            {header:'Email sent', dataIndex:'emailSent', type:'boolean',renderer:booleanToString, flex:.1,filterable:false}
        ];

        this.dockedItems = [{
            xtype:'toolbar',
            dock:'top',
            items:[{
               text:'Refresh',
               icon: resources + '/images/refresh.gif',
               handler:function() {
                   var store = Ext.getCmp('registrationlist').getStore();
                   store.loadPage(store.currentPage);
               }
            },'-',{
                text:'Clear Filters',
                icon: resources + '/images/icons-shadowless/cross.png',
                handler:function() {
                    Ext.getCmp('registrationlist').filters.clearFilters();
                }
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