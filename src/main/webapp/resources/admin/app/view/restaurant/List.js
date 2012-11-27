Ext.define('AD.view.restaurant.List' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.restaurantlist',
    store:'Restaurants',
    id:'restaurantlist',
    layout:'fit',
    loadMask:true,

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

