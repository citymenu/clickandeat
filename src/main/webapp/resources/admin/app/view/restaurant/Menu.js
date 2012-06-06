Ext.define('AD.view.restaurant.Menu' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantmenu',
    title:'Menu',

    layout:{
        type:'vbox',
        align:'stretch'
    },

    defaults:{
        border:false,
        frame:false
    },

    items: [{
        xtype:'panel',
        flex:0.4,

        layout:{
            type:'hbox',
            align:'stretch'
        },

        defaults:{
            border:true,
            frame:false,
            autoScroll:true
        },

        items:[{
            flex:0.25,
            xtype:'gridpanel',
            title:'Menu Categories',
            id:'menucategoriesgrid',
            store:'MenuCategories',

            dockedItems:[{
                xtype:'toolbar',
                dock:'top',
                items:[{
                    xtype:'button',
                    text:'Add',
                    action:'create'
                }]
            }],

            viewConfig: {
                plugins: {
                    ptype:'gridviewdragdrop',
                    dragGroup:'menuCategoriesGridDDGroup',
                    dropGroup:'menuCategoriesGridDDGroup'
                }
            },

            columns:[
                {text:'Category Name',dataIndex:'name',sortable:false,hideable:false,draggable:false,flex:1}
            ]
        },{
            xtype:'splitter'
        },{
            flex:0.75,
            xtype:'gridpanel',
            title:'Menu Items',
            id:'menuitemsgrid',
            store:'MenuItems',

            dockedItems:[{
                xtype:'toolbar',
                dock:'top',
                items:[{
                    xtype:'button',
                    text:'Add',
                    action:'create'
                }]
            }],

            viewConfig: {
                plugins: {
                    ptype:'gridviewdragdrop',
                    dragGroup:'menuItemsGridDDGroup',
                    dropGroup:'menuItemsGridDDGroup'
                }
            },

            columns:[
                {text:'Title',dataIndex:'title',sortable:false,hideable:false,draggable:false,flex:.7},
                {text:'Cost',dataIndex:'cost',sortable:false,hideable:false,draggable:false,flex:.3}
            ]
        }]
    },{
        xtype:'splitter'
    },{
        xtype:'panel',
        id:'menueditform',
        layout:'fit',
        flex:0.6,
        border:true,
        items:[]
    }]

});