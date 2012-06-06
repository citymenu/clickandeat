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
            text:'Delete',
            action:'remove'
        },{
            xtype:'button',
            text:'Undo Changes',
            action:'revert'
        }]
    }],

    defaults:{
        anchor: '100%',
        allowBlank:true
    },
    defaultType: 'textfield',

    items: [{
        fieldLabel:'Name',
        name:'name'
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
        xtype: 'htmleditor',
        labelAlign:'top',
        fontFamilies:['Segoe UI','Tahoma','Arial','Verdana','sans-serif'],
        defaultFont:'Segoe UI',
        enableColors: false,
        enableLinks:false,
        enableLists:false,
        enableAlignments: false
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