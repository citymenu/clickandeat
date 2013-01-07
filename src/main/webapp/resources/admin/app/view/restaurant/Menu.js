Ext.define('AD.view.restaurant.Menu' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantmenu',
    title:'Menu',

    layout:{
        type:'hbox',
        align:'stretch'
    },

    defaults:{
        border:false,
        frame:false
    },

    items: [{
        flex:0.2,
        xtype:'gridpanel',
        border:true,
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
            markDirty:false,
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
        flex:0.8,
        xtype:'panel',
        layout:{
            type:'vbox',
            align:'stretch'
        },

        defaults:{
            border:false,
            frame:false
        },

        items:[{
            flex:0.3,
            xtype:'gridpanel',
            border:true,
            autoScroll:true,
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
                },{
                    xtype:'button',
                    text:'Copy menu from restaurant',
                    action:'copyRestaurantMenu'
                }
                ]
            }],

            viewConfig: {
                markDirty:false,
                plugins: {
                    ptype:'gridviewdragdrop',
                    dragGroup:'menuItemsGridDDGroup',
                    dropGroup:'menuItemsGridDDGroup'
                }
            },

            columns:[
                {text:'Number',dataIndex:'number',sortable:false,hideable:false,draggable:false,flex:.1},
                {text:'Title',dataIndex:'title',sortable:false,hideable:false,draggable:false,flex:.7},
                {text:'Cost',dataIndex:'cost',sortable:false,hideable:false,draggable:false,flex:.2}
            ]
        },{
            xtype:'splitter'
        },{
            xtype:'panel',
            id:'menueditform',
            layout:'fit',
            flex:0.7,
            border:true,
            defaults:{
                autoScroll:true
            },
            items:[]
        }]
    }]

});