Ext.define('AD.view.restaurant.SpecialOfferItemEdit' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantspecialofferitemedit',
    title:'Edit Special Offer Item',

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
        name:'specialOfferId'
    },{
        fieldLabel:'Title',
        name:'title',
        allowBlank:false
    },{
        fieldLabel:'Description',
        name:'description',
        labelAlign:'top',
        xtype:'textarea',
        height:90
    },{
        fieldLabel:'Choices for this item (enter one per line)',
        labelAlign:'top',
        name:'specialOfferItemChoices',
        xtype:'textareafield',
        height:90
    }]

});