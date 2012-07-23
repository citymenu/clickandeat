Ext.define('AD.view.restaurant.MainDetails' ,{
    extend:'Ext.form.Panel',
    alias:'widget.restaurantmaindetails',
    title:'Main Details',

    bodyPadding: 15,
    layout:'anchor',
    autoScroll:true,

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
            fieldLabel:'Cuisines',
            xtype:'checkboxgroup',
            columns:3,
            vertical:true,
            items:[]
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
            name:'address1'
        },{
            fieldLabel:'Address 2',
            name:'address2'
        },{
            fieldLabel:'Address 3',
            name:'address3'
        },{
            fieldLabel:'Town',
            name:'town'
        },{
            fieldLabel:'Region',
            name:'region'
        },{
            fieldLabel:'Postcode',
            name:'postCode'
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
            name:'contactTelephone'
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
            fieldLabel:'Telephone Number',
            name:'notificationPhoneNumber'
        },{
            fieldLabel:'SMS Number',
            name:'notificationSMSNumber'
        },{
            fieldLabel:'Email Address',
            name:'notificationEmailAddress',
            vtype:'email'
        }]
    }]

});