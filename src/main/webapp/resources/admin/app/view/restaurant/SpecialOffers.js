Ext.define('AD.view.restaurant.SpecialOffers' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantspecialoffers',
    title:'Special Offers',

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
            title:'Special Offers',
            id:'specialoffersgrid',
            store:'SpecialOffers',

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
                    dragGroup:'specialOffersGridDDGroup',
                    dropGroup:'specialOffersGridDDGroup'
                }
            },

            columns:[
                {text:'Title',dataIndex:'title',sortable:false,hideable:false,draggable:false,flex:1}
            ]
        },{
            xtype:'splitter'
        },{
            flex:0.75,
            xtype:'gridpanel',
            title:'Special Offer Items',
            id:'specialofferitemsgrid',
            store:'SpecialOfferItems',

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
                    dragGroup:'specialOfferItemsGridDDGroup',
                    dropGroup:'specialOfferItemsGridDDGroup'
                }
            },

            columns:[
                {text:'Title',dataIndex:'title',sortable:false,hideable:false,draggable:false,flex:1}
            ]
        }]
    },{
        xtype:'splitter'
    },{
        xtype:'panel',
        id:'specialoffereditform',
        layout:'fit',
        flex:0.6,
        border:true,
        items:[]
    }]

});