/*
 * NotificationOptions data object
 */

Ext.define('AD.model.NotificationOptions', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'receiveNotificationCall', type:'boolean'},
        {name:'receiveSMSNotification', type:'boolean'},
        {name:'notificationPhoneNumber', type:'string'},
        {name:'notificationSMSNumber', type:'string'},
        {name:'notificationEmailAddress', type:'string'},
        {name:'printerEmailAddress', type:'string'}
    ]
});

