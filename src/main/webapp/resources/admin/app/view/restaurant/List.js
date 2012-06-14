Ext.define('AD.view.restaurant.List' ,{
    extend:'Ext.grid.Panel',
    alias:'widget.restaurantlist',
    store:'Restaurants',
    layout:'fit',
    loadMask:true,
    
    dockedItems:[{
    	xtype:'toolbar',
    	dock:'top',
    	items:[{
    		xtype:'button',
    		text:'Refresh',
    		action:'refresh'
    	},{
    		xtype:'button',
    		text:'Create New',
    		action:'create'
    	},{
    		xtype:'button',
    		text:'Edit Selected',
    		action:'edit'
    	},{
    		xtype:'button',
    		text:'Delete Selected',
    		action:'delete'
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
            {header:'Name', dataIndex:'name',flex:.9}
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