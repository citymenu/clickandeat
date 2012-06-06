Ext.define('AD.view.restaurant.DeliveryDetails' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantdeliverydetails',
    title:'Delivery/Opening Times',

    bodyPadding: 15,
    layout:'anchor',
    autoScroll:true,

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
            xtype: 'htmleditor',
            labelAlign:'top',
            fontFamilies:['Segoe UI','Tahoma','Arial','Verdana','sans-serif'],
            defaultFont:'Segoe UI',
            enableColors: false,
            enableLinks:false,
            enableLists:false,
            enableAlignments: false
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
                fieldLabel:'Collection open',
                name:'collectionOpeningTime_1'
            },{
                xtype:'timefield',
                fieldLabel:'Collection close',
                name:'collectionClosingTime_1'
            },{
                xtype:'timefield',
                fieldLabel:'Delivery open',
                name:'deliveryOpeningTime_1'
             },{
                xtype:'timefield',
                fieldLabel:'Delivery close',
                name:'deliveryClosingTime_1'
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
                name:'collectionOpeningTime_2'
            },{
                xtype:'timefield',
                name:'collectionClosingTime_2'
            },{
                xtype:'timefield',
                name:'deliveryOpeningTime_2'
             },{
                xtype:'timefield',
                name:'deliveryClosingTime_2'
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
                name:'collectionOpeningTime_3'
           },{
                xtype:'timefield',
                name:'collectionClosingTime_3'
           },{
                xtype:'timefield',
                name:'deliveryOpeningTime_3'
            },{
               xtype:'timefield',
               name:'deliveryClosingTime_3'
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
                name:'collectionOpeningTime_4'
            },{
                xtype:'timefield',
                name:'collectionClosingTime_4'
            },{
                xtype:'timefield',
                name:'deliveryOpeningTime_4'
            },{
                xtype:'timefield',
                name:'deliveryClosingTime_4'
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
                name:'collectionOpeningTime_5'
            },{
                xtype:'timefield',
                name:'collectionClosingTime_5'
            },{
                xtype:'timefield',
                name:'deliveryOpeningTime_5'
            },{
                xtype:'timefield',
                name:'deliveryClosingTime_5'
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
                name:'collectionOpeningTime_6'
            },{
                xtype:'timefield',
                name:'collectionClosingTime_6'
            },{
                xtype:'timefield',
                name:'deliveryOpeningTime_6'
            },{
                xtype:'timefield',
                name:'deliveryClosingTime_6'
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
                name:'collectionOpeningTime_7'
            },{
                xtype:'timefield',
                name:'collectionClosingTime_7'
            },{
                xtype:'timefield',
                name:'deliveryOpeningTime_7'
            },{
                xtype:'timefield',
                name:'deliveryClosingTime_7'
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
            xtype: 'htmleditor',
            fontFamilies:['Segoe UI','Tahoma','Arial','Verdana','sans-serif'],
            defaultFont:'Segoe UI',
            enableColors: false,
            enableLinks:false,
            enableLists:false,
            enableAlignments: false
        },{
            fieldLabel:'Minimum order value for free delivery',
            name:'minimumOrderForFreeDelivery',
            xtype:'numberfield'
        },{
            fieldLabel:'Delivery charge for orders under minimum value',
            name:'deliveryCharge',
            xtype:'numberfield'
        },{
            fieldLabel:'Discount for orders collected by customer',
            name:'collectionDiscount',
            xtype:'numberfield'
        },{
            fieldLabel:'Minimum order value for collection discount',
            name:'minimumOrderForCollectionDiscount',
            xtype:'numberfield'
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