Ext.define('AD.view.restaurant.DeliveryDetails' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantdeliverydetails',
    title:'Opening Times/Delivery',

    bodyPadding: 15,
    layout:'anchor',
    autoScroll:true,

    defaults:{
        anchor:'100%'
    },

    items: [{
        xtype:'fieldset',
        title:'Opening Times',
        defaults:{
            allowBlank:true,
            anchor:'100%'
        },
        defaultType: 'textfield',
        items:[{
            fieldLabel:'Summary',
            name:'openingTimesSummary',
            labelAlign:'top',
            xtype:'textarea',
            height:90
        },{
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
                fieldLabel:'Open',
                name:'open_1',
                width:60
            },{
                xtype:'timefield',
                fieldLabel:'Early open',
                name:'earlyOpeningTime_1'
            },{
                xtype:'timefield',
                fieldLabel:'Early close',
                name:'earlyClosingTime_1'
            },{
                xtype:'timefield',
                fieldLabel:'Late open',
                name:'lateOpeningTime_1'
             },{
                xtype:'timefield',
                fieldLabel:'Late close',
                name:'lateClosingTime_1'
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
                name:'open_2',
                width:60
            },{
                xtype:'timefield',
                name:'earlyOpeningTime_2'
            },{
                xtype:'timefield',
                name:'earlyClosingTime_2'
            },{
                xtype:'timefield',
                name:'lateOpeningTime_2'
             },{
                xtype:'timefield',
                name:'lateClosingTime_2'
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
                name:'open_3',
                width:60
            },{
                xtype:'timefield',
                name:'earlyOpeningTime_3'
           },{
                xtype:'timefield',
                name:'earlyClosingTime_3'
           },{
                xtype:'timefield',
                name:'lateOpeningTime_3'
            },{
               xtype:'timefield',
               name:'lateClosingTime_3'
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
                name:'open_4',
                width:60
            },{
                xtype:'timefield',
                name:'earlyOpeningTime_4'
            },{
                xtype:'timefield',
                name:'earlyClosingTime_4'
            },{
                xtype:'timefield',
                name:'lateOpeningTime_4'
            },{
                xtype:'timefield',
                name:'lateClosingTime_4'
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
                name:'open_5',
                width:60
            },{
                xtype:'timefield',
                name:'earlyOpeningTime_5'
            },{
                xtype:'timefield',
                name:'earlyClosingTime_5'
            },{
                xtype:'timefield',
                name:'lateOpeningTime_5'
            },{
                xtype:'timefield',
                name:'lateClosingTime_5'
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
                name:'open_6',
                width:60
            },{
                xtype:'timefield',
                name:'earlyOpeningTime_6'
            },{
                xtype:'timefield',
                name:'earlyClosingTime_6'
            },{
                xtype:'timefield',
                name:'lateOpeningTime_6'
            },{
                xtype:'timefield',
                name:'lateClosingTime_6'
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
                name:'open_7',
                width:60
            },{
                xtype:'timefield',
                name:'earlyOpeningTime_7'
            },{
                xtype:'timefield',
                name:'earlyClosingTime_7'
            },{
                xtype:'timefield',
                name:'lateOpeningTime_7'
            },{
                xtype:'timefield',
                name:'lateClosingTime_7'
            }]
        },{
            name:'closedDates',
            fieldLabel:'Closed dates (enter one per line in format dd/mm/yyyy)',
            xtype:'textareafield',
            labelAlign:'top',
            height:90
        }]
    },{
        xtype:'fieldset',
        title:'Delivery Details',
        defaults:{
            allowBlank:true,
            anchor:'100%',
            labelAlign:'top'
        },
        defaultType: 'textfield',
        items:[{
            fieldLabel:'Summary',
            name:'deliveryOptionsSummary',
            labelAlign:'top',
            xtype:'textarea',
            height:90
        },{
            fieldLabel:'Delivery time (minutes)',
            name:'deliveryTimeMinutes',
            xtype:'numberfield',
            allowBlank:false
        },{
            fieldLabel:'Collection time (minutes)',
            name:'collectionTimeMinutes',
            xtype:'numberfield',
            allowBlank:false
        },{
            fieldLabel:'Delivery charge',
            name:'deliveryCharge',
            xtype:'numberfield'
        },{
            fieldLabel:'Minimum order value for delivery',
            name:'minimumOrderForDelivery',
            xtype:'numberfield'
        },{
            fieldLabel:'Allow free delivery',
            name:'allowFreeDelivery',
            xtype:'checkbox'
        },{
            fieldLabel:'Minimum order value for free delivery',
            name:'minimumOrderForFreeDelivery',
            xtype:'numberfield'
        },{
            fieldLabel:'Allow delivery orders below minimum value for free delivery',
            name:'allowDeliveryBelowMinimumForFreeDelivery',
            xtype:'checkbox'
        },{
            fieldLabel:'Delivery radius (kilometres)',
            name:'deliveryRadiusInKilometres',
            xtype:'numberfield'
        },{
            name:'areasDeliveredTo',
            fieldLabel:'Areas delivered to (enter one per line)',
            xtype:'textareafield',
            height:90
        }]
    }]

});