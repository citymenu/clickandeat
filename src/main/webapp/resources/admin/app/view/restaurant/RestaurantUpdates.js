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
            {text:'Date',dataIndex:'text',sortable:true,hideable:false,draggable:false,flex:1},
            {text:'Update',dataIndex:'updateTime',sortable:true,hideable:false,draggable:false,flex:1}

        ]
    }]

});