Ext.define('LC.view.Home', {
    extend: 'Ext.Panel',
    xtype: 'homepanel',

    requires: [
        'Ext.Button'
    ],

    config:{
        items:[{
            xtype:'button',
            id:'searchbutton',
            text:'Search'
        }]
    }

});
