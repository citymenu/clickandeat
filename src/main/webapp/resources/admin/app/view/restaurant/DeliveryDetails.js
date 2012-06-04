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
            allowBlank:false
        },
        defaultType: 'textfield',
        items:[{
            xtype:'fieldcontainer',
            fieldLabel:'Sunday',
            labelStyle:'padding-top:25px;',
            layout:'hbox',

            defaults:{
                padding:'0 15 0 0'
            },

            fieldDefaults: {
                msgTarget: 'under',
                labelAlign: 'top'
            },

            items:[{
                xtype:'checkbox',
                fieldLabel:'Open',
                name:'open_0',
                width:60
            },{
                xtype:'timefield',
                fieldLabel:'Collection open',
                name:'collectionOpeningTime_0'
            },{
                xtype:'timefield',
                fieldLabel:'Collection close',
                name:'collectionClosingTime_0'
            },{
                xtype:'timefield',
                fieldLabel:'Delivery open',
                name:'deliveryOpeningTime_0'
             },{
                xtype:'timefield',
                fieldLabel:'Delivery close',
                name:'deliveryClosingTime_0'
             }]
        },{
              xtype:'fieldcontainer',
              fieldLabel:'Monday',
              layout:'hbox',

              defaults:{
                  padding:'0 15 0 0'
              },

              fieldDefaults: {
                  msgTarget: 'under',
                  labelAlign: 'top'
              },

              items:[{
                  xtype:'checkbox',
                  name:'open_1',
                  width:60
              },{
                  xtype:'timefield',
                  name:'collectionOpeningTime_1'
              },{
                  xtype:'timefield',
                  name:'collectionClosingTime_1'
              },{
                  xtype:'timefield',
                  name:'deliveryOpeningTime_1'
               },{
                  xtype:'timefield',
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
                   labelAlign: 'top'
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
         }]
    }]

});