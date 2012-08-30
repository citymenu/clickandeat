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
        anchor: '70%',
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
        xtype:'fieldcontainer',
        id:'menuitemtype',
        fieldLabel:'Type',
        defaultType:'radiofield',
        layout:'hbox',

        defaults:{
            width:85
        },

        items:[{
            boxLabel:'Standard',
            name:'type',
            inputValue:'STANDARD'
        },{
            boxLabel:'SubType',
            name:'type',
            inputValue:'SUBTYPE'
        }]
    },{
        fieldLabel:'Icon class',
        name:'iconClass'
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
        fieldLabel:'Subtypes for this item (enter one per line)',
        labelAlign:'top',
        id:'subTypeNames',
        name:'subTypeNames',
        xtype:'textareafield',
        height:90
    },{
        fieldLabel:'Costs for each subtype (enter one for each subtype above)',
        labelAlign:'top',
        id:'subTypeCosts',
        name:'subTypeCosts',
        xtype:'textareafield',
        height:90
    },{
        fieldLabel:'Additional choices for this item (enter one per line)',
        labelAlign:'top',
        name:'additionalItemChoices',
        xtype:'textareafield',
        height:90
    },{
        fieldLabel:'Additional item cost',
        labelAlign:'top',
        labelAlign:'top',
        xtype:'numberfield',
        name:'additionalItemCost'
    },{
        fieldLabel:'Maximum number of additional items that can be selected',
        labelAlign:'top',
        xtype:'numberfield',
        name:'additionalItemChoiceLimit'
    }]

});