/*
 * User registration data object
 */

Ext.define('AD.model.Registration', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'emailAddress', type:'string'},
        {name:'created', type:'date'},
        {name:'requestedDiscount', type:'number'}
    ],
    hasMany:[{
        model:'AD.model.GeoLocation',
        name:'location'
    },{
        model:'AD.model.Order',
        name:'order'
    }]
});

