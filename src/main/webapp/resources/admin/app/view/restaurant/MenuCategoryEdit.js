Ext.define('AD.view.restaurant.MenuCategoryEdit' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantmenucategoryedit',
    title:'Edit Menu Category',

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
        fieldLabel:'Name',
        name:'name',
        allowBlank:false
    },{
        xtype:'fieldcontainer',
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
            boxLabel:'Grid Layout',
            name:'type',
            inputValue:'GRID'
        }]
    },{
        fieldLabel:'Summary',
        name:'summary',
        labelAlign:'top',
        xtype:'textareafield',
        height:90
    },{
        fieldLabel:'Icon class',
        name:'iconClass'
    },{
        fieldLabel:'Grid type values (enter one per line)',
        labelAlign:'top',
        name:'itemTypes',
        xtype:'textareafield',
        height:90
    }]

});