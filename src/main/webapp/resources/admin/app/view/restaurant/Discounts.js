Ext.define('AD.view.restaurant.Discounts' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantdiscounts',
    title:'Discounts',

    layout:{
        type:'vbox',
        align:'stretch'
    },

    defaults:{
        border:false,
        frame:false
    },

    items: [{
        xtype:'gridpanel',
        flex:0.3,
        xtype:'gridpanel',
        title:'Discounts',
        id:'discountsgrid',
        store:'Discounts',

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
            markDirty:false
        },

        columns:[
            {text:'Title',dataIndex:'title',sortable:false,hideable:false,draggable:false,flex:1}
        ]
    },{
        xtype:'splitter'
    },{
        xtype:'panel',
        id:'discounteditform',
        layout:'fit',
        flex:0.7,
        border:true,
        items:[]
    }]

});