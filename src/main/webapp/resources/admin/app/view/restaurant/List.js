Ext.define('AD.view.restaurant.List' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.restaurantlist',
    store:'Restaurants',
    id:'restaurantlist',
    layout:'fit',
    loadMask:true,
    stateful:true,
    stateId:'restaurantgridpanel',

    features:[{
        ftype: 'filters',
        encode: false,
        stateful: true,
        stateId: 'restaurantListFilters'
    }],

    initComponent: function() {

        var store = Ext.widget('restaurantstore');
        this.store = store;

        this.columns = [
            {header:'ID', dataIndex:'restaurantId',flex:.1,filter:{type:'string'}},
            {header:'Name', dataIndex:'name',flex:.1,filter:{type:'string'}},
            {header:'Town', dataIndex:'town',flex:.1,filterable:false},
            {header:'Postcode', dataIndex:'postcode',flex:.1,filterable:false},
            {header:'Recommended', dataIndex:'recommended', renderer:booleanToString, type:'boolean',flex:.1,filter:{type:'boolean'}},
            {header:'List on site', dataIndex:'listOnSite', renderer:booleanToString, flex:.1,filter:{type:'boolean'}},
            {header:'Content approved', dataIndex:'contentApproved', renderer:booleanToString, flex:.1,filter:{type:'boolean'}},
            {header:'Content status', dataIndex:'contentStatus', flex:.1,filterable:false},
            {header:'Origin', dataIndex:'origin', flex:.1,filterable:false},
            {header:'Rating', dataIndex:'justEatRating', flex:.1,filter:{type:'numeric'}},
            {header:'Content status date', dataIndex:'lastContentApprovalStatusUpdated', renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'), flex:.1,filter:{type:'date'}},
            {header:'Search ranking', dataIndex:'searchRanking', flex:.1,filter:{type:'numeric'}},
            {header:'Phone orders only', dataIndex:'phoneOrdersOnly', renderer:booleanToString, type:'boolean',flex:.1,filter:{type:'boolean'}},
            {header:'In test mode', dataIndex:'testMode', renderer:booleanToString, type:'boolean',flex:.1,filter:{type:'boolean'}},
            {header:'Receive Call', dataIndex:'notificationOptions.receiveNotificationCall', renderer:renderNotificationCall, type:'boolean',flex:.1,filter:{type:'boolean'}},
            {header:'Receive SMS', dataIndex:'notificationOptions.receiveSMSNotification', renderer:renderSMSNotification, type:'boolean',flex:.1,filter:{type:'boolean'}},
            {header:'Created', dataIndex:'created',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1,filter:{type:'date'}},
            {header:'Last updated', dataIndex:'lastUpdated',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1,filter:{type:'date'}}
        ];

        this.dockedItems = [{
            xtype:'toolbar',
            dock:'top',
            items:[{
               text:'Refresh',
               icon: resources + '/images/refresh.gif',
               handler:function() {
                   var store = Ext.getCmp('restaurantlist').getStore();
                   store.loadPage(store.currentPage);
               }
            },'-',{
                text:'Clear Filters',
                icon: resources + '/images/icons-shadowless/cross.png',
                handler:function() {
                    Ext.getCmp('restaurantlist').filters.clearFilters();
                }
            },{
                text:'Create New',
                icon:'../resources/images/icons-shadowless/document--plus.png',
                action:'create'
            },'-',{
                xtype:'button',
                icon: resources + '/images/icons-shadowless/report-excel.png',
                text:'Import/Export',
                menu:{
                    xtype:'menu',
                    items: [{
                        text:'Upload Restaurant Sheet',
                        action:'uploadTemplate',
                        icon: resources + '/images/icons-shadowless/upload.png'
                    },{
                        text:'Download Template',
                        action:'downloadTemplate',
                        icon: resources + '/images/icons-shadowless/download.png'
                    }]
                }
            },'->',{
                width:300,
                fieldLabel:'Search by Name',
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

