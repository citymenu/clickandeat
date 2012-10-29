Ext.define('AD.view.order.OrderAmendment' ,{
    extend:'Ext.form.Panel',
    alias:'widget.orderamendmentedit',
    id:'orderamendment',

    bodyPadding: 15,
    layout:'anchor',
    autoScroll:true,
    border:false,
    frame:false,

    defaults:{
        allowBlank:false,
        anchor:'100%',
        labelAlign:'top'
    },

    items: [{
        name:'orderId',
        xtype:'hidden'
    },{
        fieldLabel:'Description',
        name:'description',
        labelAlign:'top',
        xtype:'textarea',
        height:130
    },{
        fieldLabel:'Amended restaurant cost',
        name:'restaurantCost',
        labelAlign:'top',
        xtype:'numberfield'
    },{
        fieldLabel:'Amended total order cost',
        name:'totalCost',
        labelAlign:'top',
        xtype:'numberfield'
    }]

});