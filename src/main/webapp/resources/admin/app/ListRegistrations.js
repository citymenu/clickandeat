Ext.Loader.setConfig({enabled:true});
Ext.Loader.setPath('Ext.ux', '../resources/ext/src/ux');

var cp = Ext.create('Ext.state.CookieProvider', {
    expires: new Date(new Date().getTime()+(1000*60*60*24)) //1 days
});

Ext.state.Manager.setProvider(cp);

Ext.require([
    'Ext.ux.RowExpander',
    'Ext.ux.grid.FiltersFeature'
]);

Ext.application({
    name: 'AD',
    appFolder: ctx + '/resources/admin/app',
    controllers:['RegistrationList'],
    
    launch: function() {
        Ext.create('Ext.container.Viewport',{
            layout:'border',
            items:[{
                region: 'north',
                contentEl: 'north',
                collapsible: false,
                height: 133,
                frame: false,
                border:false
            },{
                region:'center',
                xtype:'registrationlist',
                frame:true
            }]
        });
    }
});