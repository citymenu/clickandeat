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
             xtype:'fieldcontainer',
             fieldLabel:'Bank Holidays',
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
                 name:'open_bankHoliday',
                 width:60
             },{
                 xtype:'timefield',
                 name:'earlyOpeningTime_bankHoliday'
             },{
                 xtype:'timefield',
                 name:'earlyClosingTime_bankHoliday'
             },{
                 xtype:'timefield',
                 name:'lateOpeningTime_bankHoliday'
             },{
                 xtype:'timefield',
                 name:'lateClosingTime_bankHoliday'
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
             fieldLabel:'Collection orders only (no deliveries)',
             name:'collectionOnly',
             xtype:'checkbox'
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
            fieldLabel:'Standard delivery charge',
            name:'deliveryCharge',
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
    },{
        xtype:'fieldset',
        title:'Minimum order values for delivery',
        defaults:{
            allowBlank:true,
            anchor:'100%',
            labelAlign:'top'
        },
        defaultType: 'textfield',
        items:[{
            fieldLabel:'Minimum order value for delivery',
            name:'minimumOrderForDelivery',
            xtype:'numberfield'
        },{
            name:'minimumDeliveryAreas',
            fieldLabel:'Enter comma-separated lists of locations with the same minimum order value (one list per line)',
            xtype:'textareafield',
            height:90
        },{
           name:'minimumDeliveryAmounts',
           fieldLabel:'Enter minimum order values for the location lists in the field above (one value per line)',
           xtype:'textareafield',
           height:90
        }]
    },{
        xtype:'fieldset',
        title:'Delivery Charges by Order Amount',
        defaults:{
            allowBlank:true,
            anchor:'100%',
            labelAlign:'top'
        },
        defaultType: 'textfield',
        items:[{
            name:'variableChargeOrderValues',
            fieldLabel:'Enter minimum order values (one per line)',
            xtype:'textareafield',
            height:90
        },{
             name:'variableChargeAmounts',
             fieldLabel:'Enter delivery charges for the minium order values in the field above (one charge per line)',
             xtype:'textareafield',
             height:90
        }]
    },{
        xtype:'fieldset',
        title:'Delivery Charges by Location',
        defaults:{
            allowBlank:true,
            anchor:'100%',
            labelAlign:'top'
        },
        defaultType: 'textfield',
        items:[{
            name:'deliveryChargeAreas',
            fieldLabel:'Enter comma-separated lists of locations with the same delivery charge (one list per line)',
            xtype:'textareafield',
            height:90
        },{
             name:'deliveryChargeAmounts',
             fieldLabel:'Enter delivery charges for the location lists in the field above (one charge per line)',
             xtype:'textareafield',
             height:90
        }]
    }]
});