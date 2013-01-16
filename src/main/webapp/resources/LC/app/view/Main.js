Ext.define('LC.view.Main', {
    extend: 'Ext.tab.Panel',
    xtype: 'main',

    requires:['LC.view.Home'],

    config: {
        fullscreen: true,
        tabBarPosition: 'bottom',
        cls:'home',
        items: [{
            title:'Home',
            iconCls:'home',
            xtype:'homepanel'
        }]
    }
});
