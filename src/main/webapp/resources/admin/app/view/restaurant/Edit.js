Ext.define('AD.view.restaurant.Edit' ,{
    extend:'Ext.panel.Panel',
    alias:'widget.restaurantedit',
    layout:'fit',

    dockedItems:[{
        xtype:'toolbar',
        dock:'top',
        items:[{
            xtype:'button',
            text:'Save Changes',
            action:'save'
        },{
            xtype:'button',
            text:'Close',
            action:'close'
        },{
            xtype:'button',
            text:'Preview',
            action:'preview'
        }]
    }],

    items:[{
        xtype:'tabpanel',
        layout:'fit',
        items:[{
            xtype:'restaurantmaindetails'
        },{
            xtype:'restaurantdeliverydetails'
        },{
            xtype:'restaurantmenu'
        }]
    }]

});