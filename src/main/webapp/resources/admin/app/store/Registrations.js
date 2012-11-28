Ext.define('AD.store.Registrations', {
    extend:'Ext.data.Store',
    alias:'widget.registrationstore',
    model:'AD.model.Registration',
    autoDestroy:true,
    autoLoad:false,
    autoSync:false,
    remoteSort:true,
    pageSize:100,

    proxy:{
    	type:'ajax',
        url:ctx+'/admin/registrations/list.ajax',
        reader: {
            type:'json',
            root:'registrations',
            successProperty:'success',
            totalProperty:'count'
        }
    },
    sortInfo:{
    	field:'created',
    	direction:'DESC'
    },
    listeners: {
    	load: function(store,storeRecs) {
    		var i,r;
    		for (i=0;i<storeRecs.length;i++) {
    			r = storeRecs[i];
    			var associatedData = r.getAssociatedData();
    			var orders = r.getAssociatedData().order;
    			var locations = r.getAssociatedData().location;
    			if( orders.length > 0 ) {
    			    r.set('order',orders[0]);
    			}
    			if( locations.length > 0 ) {
    			    r.set('location',locations[0]);
    			}
    		}
    	}
    }

});