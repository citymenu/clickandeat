Ext.define('AD.model.Restaurant', {
    extend: 'Ext.data.Model',
    idProperty:'id',
    fields: ['id','class','serviceId','name','streetAddress','town','county','postCode','phone','fax','email','mainContactName'],
    proxy:{
    	type:'ajax',
        api: {
            create:ctx+'/data/save.ajax',
            update:ctx+'/data/save.ajax',
            destroy:ctx+'/data/delete.ajax'
        }
    }
});