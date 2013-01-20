Ext.define('AD.store.AcceptedOrders', {
    extend:'Ext.data.Store',
    alias:'widget.acceptedorderstore',
    model:'AD.model.Order',
    autoDestroy:true,
    autoLoad:false,
    autoSync:false,
    remoteSort:false,

    proxy:{
    	type:'ajax',
        url:ctx+'/admin/orders/acceptedOrders.ajax',
        reader: {
            type:'json',
            root:'orders',
            successProperty:'success',
            totalProperty:'count'
        }
    },
    groupField:'orderCreatedMonth',
    sortInfo:{
    	field:'orderCreatedDate',
    	direction:'ASC'
    }

});