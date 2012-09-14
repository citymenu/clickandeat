Ext.define('AD.view.restaurant.SpecialOfferEdit' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantspecialofferedit',
    title:'Edit Special Offer',

    bodyPadding: 15,
    layout:'anchor',
    autoScroll:true,

    defaults:{
        anchor:'70%'
    },

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

    items: [{
        xtype:'fieldset',
        title:'Special offer details',
        defaults:{
            allowBlank:true,
            anchor:'100%',
            labelAlign:'top'
        },
        defaultType: 'textfield',
        items:[{
            xtype:'hidden',
            name:'specialOfferId'
        },{
            fieldLabel:'Number',
            xtype:'numberfield',
            name:'number'
        },{
            fieldLabel:'Title',
            name:'title',
            labelAlign:'left'
        },{
            fieldLabel:'Description',
            name:'description',
            labelAlign:'top',
            xtype:'textarea',
            height:90
        },{
            fieldLabel:'Cost',
            name:'cost',
            labelAlign:'left',
            xtype:'numberfield'
        }]
    },{
        xtype:'fieldset',
        title:'Days and times that special offer is applicable (leave times blank for all day)',
        defaults:{
            allowBlank:true,
            anchor:'100%'
        },
        defaultType: 'textfield',
        items:[{
            xtype:'fieldcontainer',
            fieldLabel:'Monday',
            labelStyle:'padding-top:23px;',
            layout:'hbox',

            defaults:{
                padding:'0 15 0 0',
            },

            fieldDefaults: {
                msgTarget: 'under',
                labelAlign: 'top',
                format:'H:i'
            },

            items:[{
                xtype:'checkbox',
                fieldLabel:'Applicable',
                name:'applicable_1',
                width:90
            },{
                xtype:'timefield',
                fieldLabel:'From',
                name:'applicableFrom_1'
            },{
                xtype:'timefield',
                fieldLabel:'To',
                name:'applicableTo_1'
            }]
        },{
            xtype:'fieldcontainer',
            fieldLabel:'Tuesday',
            layout:'hbox',

            defaults:{
                padding:'0 15 0 0'
            },

            fieldDefaults: {
                msgTarget: 'under',
                labelAlign: 'top',
                format:'H:i'
            },

            items:[{
                xtype:'checkbox',
                name:'applicable_2',
                width:90
            },{
                xtype:'timefield',
                name:'applicableFrom_2'
            },{
                xtype:'timefield',
                name:'applicableTo_2'
            }]
        },{
            xtype:'fieldcontainer',
            fieldLabel:'Wednesday',
            layout:'hbox',

            defaults:{
                padding:'0 15 0 0'
            },

            fieldDefaults: {
                msgTarget: 'under',
                labelAlign: 'top',
                format:'H:i'
            },

            items:[{
                xtype:'checkbox',
                name:'applicable_3',
                width:90
            },{
                xtype:'timefield',
                name:'applicableFrom_3'
           },{
                xtype:'timefield',
                name:'applicableTo_3'
           }]
         },{
            xtype:'fieldcontainer',
            fieldLabel:'Thursday',
            layout:'hbox',

            defaults:{
                padding:'0 15 0 0'
            },

            fieldDefaults: {
                msgTarget:'under',
                labelAlign:'top',
                format:'H:i'
            },

            items:[{
                xtype:'checkbox',
                name:'applicable_4',
                width:90
            },{
                xtype:'timefield',
                name:'applicableFrom_4'
            },{
                xtype:'timefield',
                name:'applicableTo_4'
            }]
        },{
            xtype:'fieldcontainer',
            fieldLabel:'Friday',
            layout:'hbox',

            defaults:{
                padding:'0 15 0 0'
            },

            fieldDefaults: {
                msgTarget:'under',
                labelAlign:'top',
                format:'H:i'
            },

            items:[{
                xtype:'checkbox',
                name:'applicable_5',
                width:90
            },{
                xtype:'timefield',
                name:'applicableFrom_5'
            },{
                xtype:'timefield',
                name:'applicableTo_5'
            }]
        },{
            xtype:'fieldcontainer',
            fieldLabel:'Saturday',
            layout:'hbox',

            defaults:{
                padding:'0 15 0 0'
            },

            fieldDefaults: {
                msgTarget:'under',
                labelAlign:'top',
                format:'H:i'
            },

            items:[{
                xtype:'checkbox',
                name:'applicable_6',
                width:90
            },{
                xtype:'timefield',
                name:'applicableFrom_6'
            },{
                xtype:'timefield',
                name:'applicableTo_6'
            }]
        },{
            xtype:'fieldcontainer',
            fieldLabel:'Sunday',
            layout:'hbox',

            defaults:{
                padding:'0 15 0 0'
            },

            fieldDefaults: {
                msgTarget:'under',
                labelAlign:'top',
                format:'H:i'
            },

            items:[{
                xtype:'checkbox',
                name:'applicable_7',
                width:90
            },{
                xtype:'timefield',
                name:'applicableFrom_7'
            },{
                xtype:'timefield',
                name:'applicableTo_7'
            }]
        }]
    }]

});