Ext.define('AD.view.restaurant.MainDetails' ,{
    extend:'Ext.panel.Panel',
    alias:'widget.restaurantmaindetails',
    title:'Main Details',

    bodyPadding: 15,
    layout:'anchor',
    defaults:{
        anchor: '100%',
        allowBlank:true
    },
    defaultType: 'textfield',

    items: [{
        fieldLabel:'Restaurant Id',
        name:'restaurantId',
        allowBlank:false
    },{
        fieldLabel:'Name',
        name:'name',
        allowBlank: false
    },{
        fieldLabel:'Description',
        name:'description',
        xtype: 'htmleditor',
        fontFamilies:['Segoe UI','Tahoma','Arial','Verdana','sans-serif'],
        defaultFont:'Segoe UI',
        enableColors: false,
        enableLinks:false,
        enableLists:false,
        enableAlignments: false
    },{
         fieldLabel:'Telephone',
         name:'telephone'
    },{
        fieldLabel:'Email',
        name:'email',
        vtype:'email'
    },{
        fieldLabel:'Website',
        name:'website'
    },{
        fieldLabel:'Cuisines',
        name:'cuisines'
    }]

});