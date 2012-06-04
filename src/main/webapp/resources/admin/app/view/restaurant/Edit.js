Ext.define('AD.view.restaurant.Edit' ,{
    extend:'Ext.panel.Panel',
    alias:'widget.restaurantedit',
    layout:'fit',

     dockedItems:[{
        xtype:'toolbar',
        dock:'top',
        items:[{
            xtype:'button',
            text:'Close',
            action:'close'
        },{
            xtype:'button',
            text:'Save',
            action:'save'
        },{
            xtype:'button',
            text:'Preview',
            action:'preview'
        }]
    }],

    items:[{
        xtype:'tabpanel',
        layout:'fit',
        border:false,
        frame:false,
        items:[{
            xtype:'restaurantmaindetails'
        },{
            xtype:'restaurantdeliverydetails'
        }]
    }]

});