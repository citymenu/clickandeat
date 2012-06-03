/*
 * Restaurant data object
 */

Ext.define('AD.model.Restaurant', {
    extend: 'Ext.data.Model',
    idProperty:'id',
    fields: [
        {name:'id', type:'string'},
        {name:'restaurantId', type:'string'},
        {name:'name', type:'string'},
        {name:'description',type: 'string'},
        {name:'email', type:'string'},
        {name:'telephone', type:'string'},
        {name:'website', type:'string'},
        {name:'cuisines', type:'auto'},
        {name:'imageId', type:'string'},
        {name:'mainContact', type:'AD.model.Person'}
    ]
});

