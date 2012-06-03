Ext.define('AD.store.Restaurants', {
    extend:'Ext.data.Store',
    alias:'widget.restaurantstore',
    model:'AD.model.Restaurant',
    autoDestroy:true,
    autoLoad:false,
    autoSync:false,
    remoteSort:true,
    pageSize:100,

    proxy:{
    	type:'ajax',
        url:ctx+'/admin/restaurants/list.ajax',
        reader: {
            type:'json',
            root:'restaurants',
            successProperty:'success',
            totalProperty:'count'
        }
    },
    sortInfo:{
    	field:'name',
    	direction:'ASC'
    }

});