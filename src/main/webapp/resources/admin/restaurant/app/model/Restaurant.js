/*
 * Restaurant data object for JSON
 */
Ext.define('AD.model.Restaurant', {
    extend: 'Ext.data.Model',
    idProperty:'id',
    fields: [
        'id',
        'restaurantId',
        'name',
        'email',
        'telephone',
        'website',
        'cuisines',
        'imageId'
        ],
    proxy:{
    	type:'ajax',
        api: {
            create:ctx+'/data/save.ajax',
            update:ctx+'/data/save.ajax',
            destroy:ctx+'/data/delete.ajax'
        }
    }
});