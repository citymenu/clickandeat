Ext.define('AD.view.restaurant.Menu' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantmenu',
    title:'Menu',

    layout:'vbox',

    defaults:{
        border:false,
        frame:false
    },

    items: [{
        xtype:'panel',
        height:275,
        width:'100%',
        layout:'hbox',

        defaults:{
            layout:'fit',
            border:true,
            frame:false,
            autoScroll:true
        },

        items:[{
            xtype:'gridpanel',
            title:'Menu Categories',
            id:'menucategoriesgrid',
            store:'MenuCategories',

            dockedItems:[{
                xtype:'toolbar',
                dock:'top',
                items:[{
                    xtype:'button',
                    text:'Create',
                    action:'create'
                },{
                    xtype:'button',
                    text:'Remove',
                    action:'remove'
                }]
            }],

            viewConfig: {
                plugins: {
                    ptype:'gridviewdragdrop',
                    dragGroup:'menuCategoriesGridDDGroup',
                    dropGroup:'menuCategoriesGridDDGroup'
                }
            },

            layout:'fit',
            width:250,
            height:275,
            columns:[
                {text:'Category Name',dataIndex:'name',sortable:false,hideable:false,draggable:false,flex:1}
            ]
        },{
            xtype:'gridpanel',
            title:'Menu Items',
            id:'menuitemsgrid',
            store:'MenuItems',

            dockedItems:[{
                xtype:'toolbar',
                dock:'top',
                items:[{
                    xtype:'button',
                    text:'Create',
                    action:'create'
                },{
                    xtype:'button',
                    text:'Remove',
                    action:'remove'
                }]
            }],

            viewConfig: {
                plugins: {
                    ptype:'gridviewdragdrop',
                    dragGroup:'menuItemsGridDDGroup',
                    dropGroup:'menuItemsGridDDGroup'
                }
            },

            layout:'fit',
            width:880,
            height:275,
            columns:[
                {text:'Title',dataIndex:'title',sortable:false,hideable:false,draggable:false,flex:.7},
                {text:'Cost',dataIndex:'cost',sortable:false,hideable:false,draggable:false,flex:.3}
            ]
        }]
    }]

});