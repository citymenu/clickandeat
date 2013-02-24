Ext.define('AD.view.restaurant.List' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.restaurantlist',
    store:'Restaurants',
    id:'restaurantlist',
    layout:'fit',
    loadMask:true,
    stateful:true,
    stateId:'restaurantgridpanel',

    dockedItems:[{
    	xtype:'toolbar',
    	dock:'top',
    	items:[{
           text:'Refresh',
           icon: resources + '/images/refresh.gif',
           handler:function() {
               var store = Ext.getCmp('restaurantlist').getStore();
               store.loadPage(store.currentPage);
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
    	}]
    },{
        xtype:'pagingtoolbar',
        store:'Restaurants',
        dock:'bottom',
        displayInfo: true
    }],

    initComponent: function() {
        this.columns = [
            {header:'ID', dataIndex:'restaurantId',flex:.1},
            {header:'Name', dataIndex:'name',flex:.1},
            {header:'Recommended', dataIndex:'recommended', renderer:booleanToString, type:'boolean',flex:.1},
            {header:'List on site', dataIndex:'listOnSite', renderer:booleanToString, flex:.1},
            {header:'Content approved', dataIndex:'contentApproved', renderer:booleanToString, flex:.1},
            {header:'Content status', dataIndex:'contentStatus', flex:.1},
            {header:'Content status date', dataIndex:'lastContentApprovalStatusUpdated', renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'), flex:.1},
            {header:'Search ranking', dataIndex:'searchRanking', flex:.1},
            {header:'Phone orders only', dataIndex:'phoneOrdersOnly', renderer:booleanToString, type:'boolean',flex:.1},
            {header:'In test mode', dataIndex:'testMode', renderer:booleanToString, type:'boolean',flex:.1},
            {header:'Receive Call', dataIndex:'notificationOptions.receiveNotificationCall', renderer:renderNotificationCall, type:'boolean',flex:.1},
            {header:'Receive SMS', dataIndex:'notificationOptions.receiveSMSNotification', renderer:renderSMSNotification, type:'boolean',flex:.1},
            {header:'Created', dataIndex:'created',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1},
            {header:'Last updated', dataIndex:'lastUpdated',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1}
        ];

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

