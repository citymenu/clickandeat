Ext.define('AD.view.restaurant.MenuItemEdit' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantmenuitemedit',
    title:'Edit Menu Item',

    bodyPadding: 15,
    layout:'anchor',
    autoScroll:true,
    frame:false,
    border:false,

    dockedItems:[{
        xtype:'toolbar',
        dock:'top',
        items:[{
            xtype:'button',
            text:'Save Changes',
            action:'save'
        },{
            xtype:'button',
            text:'Undo Changes',
            action:'revert'
        },{
            xtype:'button',
            text:'Delete',
            action:'remove'
        }]
    }],

    defaults:{
        anchor: '100%',
        allowBlank:true
    },
    defaultType: 'textfield',

    items: [{
        xtype:'hidden',
        name:'categoryId'
    },{
        fieldLabel:'Number',
        xtype:'numberfield',
        name:'number'
    },{
        fieldLabel:'Title',
        name:'title',
        allowBlank:false
    },{
        fieldLabel:'Subtitle',
        name:'subtitle'
    },{
        fieldLabel:'Description',
        name:'description',
        labelAlign:'top',
        xtype:'textarea',
        height:90
    },{
        fieldLabel:'Cost',
        xtype:'numberfield',
        name:'cost'
    },{
        fieldLabel:'Icon class',
        name:'iconClass'
    }]

});