Ext.define('AD.store.Orders', {
    extend:'Ext.data.Store',
    alias:'widget.orderstore',
    model:'AD.model.Order',
    autoDestroy:true,
    autoLoad:false,
    autoSync:false,
    remoteSort:true,
    pageSize:100,

    proxy:{
    	type:'ajax',
        url:ctx+'/admin/orders/list.ajax',
        reader: {
            type:'json',
            root:'orders',
            successProperty:'success',
            totalProperty:'count'
        }
    },
    sortInfo:{
    	field:'orderId',
    	direction:'ASC'
    }

});