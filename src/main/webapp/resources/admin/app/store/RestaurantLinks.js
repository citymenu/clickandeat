Ext.define('AD.store.RestaurantLinks', {
    extend:'Ext.data.Store',
    alias:'widget.restaurantlinkstore',
    model:'AD.model.Restaurant',
    autoDestroy:true,
    autoLoad:true,
    autoSync:false,
    remoteSort:false,

    proxy:{
    	type:'ajax',
        url:ctx+'/admin/restaurants/quickLaunch.ajax',
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