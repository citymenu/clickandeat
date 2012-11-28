/*
 * Geolocation data object
 */

Ext.define('AD.model.GeoLocation', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'address', type:'string'},
        {name:'valid', type:'boolean'},
        {name:'fullAddress', type:'string'},
        {name:'displayAddress', type:'string'}
    ]
});

