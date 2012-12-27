Ext.define('AD.view.restaurant.QuickLaunch' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.restaurantquicklaunch',
    store:'RestaurantLinks',
    id:'restaurantquicklaunch',
    title:'Quick Launch',
    layout:'fit',
    width:230,
    collapsible:true,
    collapsed:true,
    resizable:true,
    stateful:false,
    autoScroll:true,

    viewConfig: {
        loadMask: false
    },

    initComponent: function() {
        this.columns = [
            {header:'Name', dataIndex:'name',flex:1, hideable:false, resizable:false}
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

