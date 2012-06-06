/*
 * Person data object
 */

Ext.define('AD.model.Person', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'firstName', type:'string'},
        {name:'lastName', type:'string'},
        {name:'telephone', type:'string'},
        {name:'mobile', type:'string'},
        {name:'email', type:'string'}
    ]
});
