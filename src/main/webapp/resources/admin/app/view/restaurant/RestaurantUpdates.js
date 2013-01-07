Ext.define('AD.view.restaurant.RestaurantUpdates' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantupdates',
    title:'Updates',

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
        title:'Updates',
        id:'updatesgrid',
        store:'RestaurantUpdates',

        columns:[
            {header:'Date', dataIndex:'updateTime',renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'),flex:.1, filter:{type:'date'}},
            {text:'Action',dataIndex:'text',sortable:true, hideable:false, draggable:false, flex:1,tdCls: 'wrap'}


        ]
    }]

});