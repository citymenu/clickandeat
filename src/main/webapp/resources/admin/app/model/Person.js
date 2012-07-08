/*
 * Person data object
 */

Ext.define('AD.model.Person', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'firstName', type:'string', convert: unescapeQuotes},
        {name:'lastName', type:'string', convert: unescapeQuotes},
        {name:'telephone', type:'string'},
        {name:'mobile', type:'string'},
        {name:'email', type:'string'}
    ]
});

