Ext.define('AD.view.restaurant.Edit' ,{
    extend:'Ext.panel.Panel',
    alias:'widget.restaurantedit',
    id:'restauranteditpanel',
    layout:'fit',

    dockedItems:[{
        xtype:'toolbar',
        dock:'top',
        items:[{
            xtype:'button',
            text:'Save Changes',
            action:'saverestaurant'
        },{
            xtype:'button',
            text:'Close',
            action:'close'
        },{
            xtype:'button',
            text:'Send for Owner Approval',
            action:'sendForOwnerApproval'
        }]
    }],

    items:[{
        xtype:'tabpanel',
        id:'restauranttabpanel',
        layout:'fit',
        deferredRender:false,
        items:[{
            xtype:'restaurantmaindetails'
        },{
            xtype:'restaurantdeliverydetails'
        },{
            xtype:'restaurantmenu'
        },{
            xtype:'restaurantdiscounts'
        },{
            xtype:'restaurantspecialoffers'
        }]
    }]

});