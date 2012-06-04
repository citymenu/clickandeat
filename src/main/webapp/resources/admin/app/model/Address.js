/*
 * Address data object
 */

Ext.define('AD.model.Address', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'address1', type:'string'},
        {name:'address2', type:'string'},
        {name:'address3', type:'string'},
        {name:'town', type:'string'},
        {name:'region', type:'string'},
        {name:'postCode', type:'string'}
    ]
});

