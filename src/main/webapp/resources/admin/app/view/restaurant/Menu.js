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
        height:200,
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

            viewConfig: {
                plugins: {
                    ptype:'gridviewdragdrop',
                    dragGroup:'menuCategoriesGridDDGroup',
                    dropGroup:'menuCategoriesGridDDGroup'
                }
            },

            layout:'fit',
            width:250,
            height:200,
            columns:[
                {text:'Category Name',dataIndex:'name',sortable:false,hideable:false,draggable:false,flex:1}
            ]
        },{
            xtype:'gridpanel',
            title:'Menu Items',
            id:'menuitemsgrid',
            store:'MenuItems',

            viewConfig: {
                plugins: {
                    ptype:'gridviewdragdrop',
                    dragGroup:'menuItemsGridDDGroup',
                    dropGroup:'menuItemsGridDDGroup'
                }
            },

            layout:'fit',
            width:880,
            height:200,
            columns:[
                {text:'Title',dataIndex:'title',sortable:false,hideable:false,draggable:false,flex:.7},
                {text:'Cost',dataIndex:'cost',sortable:false,hideable:false,draggable:false,flex:.3}
            ]
        }]
    }]

});