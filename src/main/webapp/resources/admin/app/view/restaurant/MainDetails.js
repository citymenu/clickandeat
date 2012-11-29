Ext.define('AD.view.restaurant.MainDetails' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantmaindetails',
    title:'Main Details',

    bodyPadding: 15,
    layout:'anchor',
    autoScroll:true,

    defaults:{
        anchor:'70%'
    },

    items: [{
        xtype:'fieldset',
        title:'Restaurant Details',
        defaults:{
            anchor: '100%',
            allowBlank:true
        },
        defaultType: 'textfield',
        items:[{
            fieldLabel:'Name',
            name:'name',
            allowBlank: false
        },{
            fieldLabel:'Description',
            name:'description',
            labelAlign:'top',
            xtype:'textareafield',
            height:90
        },{
            fieldLabel:'Image name',
            name:'imageName',
            allowBlank: true
        },{
            fieldLabel:'Cuisines',
            xtype:'checkboxgroup',
            columns:3,
            vertical:true,
            items:[]
        },{
            fieldLabel:'Include this restaurant on the main site',
            labelAlign:'top',
            xtype:'checkbox',
            name:'listOnSite'
        },{
            fieldLabel:'Only accepts phone orders',
            labelAlign:'top',
            xtype:'checkbox',
            name:'phoneOrdersOnly'
        },{
             fieldLabel:'Appear in recommendations on home page',
             labelAlign:'top',
             xtype:'checkbox',
             name:'recommended'
         },{
            fieldLabel:'Is in test mode (skip payment options)',
            labelAlign:'top',
            xtype:'checkbox',
            name:'testMode'
        },{
            fieldLabel:'Ranking score for display in search results (0-100)',
            labelAlign:'top',
            xtype:'numberfield',
            name:'searchRanking'
        }]
    },{
        xtype:'fieldset',
        title:'Address',
        defaults:{
            anchor: '100%',
            allowBlank:true
        },
        defaultType: 'textfield',
        items:[{
            fieldLabel:'Address 1',
            name:'address1',
            allowBlank: false
        },{
            fieldLabel:'Town',
            name:'town',
            allowBlank: false
        },{
            fieldLabel:'Region',
            name:'region'
        },{
            fieldLabel:'Postcode',
            name:'postCode',
            allowBlank: false
        }]
    },{
        xtype:'fieldset',
        title:'Contact Details',
        defaults:{
            anchor: '100%',
            allowBlank:true
        },
        defaultType: 'textfield',
        items:[{
            fieldLabel:'Telephone',
            name:'contactTelephone',
            allowBlank:false,
        },{
            fieldLabel:'Mobile',
            name:'contactMobile'
        },{
            fieldLabel:'Email',
            name:'contactEmail',
            vtype:'email'
        },{
            fieldLabel:'Website',
            name:'website',
            vtype:'url'
        }]
    },{
        xtype:'fieldset',
        title:'Main Contact',
        defaults:{
            anchor: '100%',
            allowBlank:true
        },
        defaultType: 'textfield',
        items:[{
            fieldLabel:'First Name',
            name:'firstName'
        },{
            fieldLabel:'Last Name',
            name:'lastName'
        },{
            fieldLabel:'Telephone',
            name:'telephone'
        },{
             fieldLabel:'Mobile',
             name:'mobile'
        },{
             fieldLabel:'Email',
             name:'email',
             vtype:'email'
        }]
    },{
        xtype:'fieldset',
        title:'Notification options',
        defaults:{
            anchor: '100%',
            allowBlank:true,
            labelWidth:125
        },
        defaultType:'textfield',
        items:[{
            xtype:'fieldcontainer',
            fieldLabel:'Notification method',
            layout:'hbox',

            defaults:{
                labelAlign:'top',
                labelWidth:150,
                width:165
            },

            items:[{
                xtype:'checkbox',
                fieldLabel:'Receive call',
                name:'receiveNotificationCall'
            },{
                xtype:'checkbox',
                fieldLabel:'Receive SMS',
                name:'receiveSMSNotification'
            }]
        },{
            fieldLabel:'Telephone number',
            name:'notificationPhoneNumber'
        },{
            fieldLabel:'SMS number',
            name:'notificationSMSNumber',
            vtype:'notification'
        },{
            fieldLabel:'Email address',
            name:'notificationEmailAddress',
            vtype:'email'
        },{
            fieldLabel:'Printer email address',
            name:'printerEmailAddress',
            vtype:'email'
        }]
    }]

});