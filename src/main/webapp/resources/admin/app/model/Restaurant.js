/*
 * Restaurant data object
 */

Ext.define('AD.model.Restaurant', {
    extend: 'Ext.data.Model',
    fields: [
        {name:'restaurantId', type:'string'},
        {name:'name', type:'string',convert: unescapeQuotes},
        {name:'description',type: 'string',convert: unescapeQuotes},
        {name:'origin',type: 'string'},
        {name:'externalId',type: 'string'},
        {name:'contactEmail', type:'string'},
        {name:'contactTelephone', type:'string'},
        {name:'contactMobile', type:'string'},
        {name:'website', type:'string'},
        {name:'cuisines', type:'auto'},
        {name:'listOnSite', type:'boolean'},
        {name:'phoneOrdersOnly', type:'boolean'},
        {name:'recommended', type:'boolean'},
        {name:'testMode', type:'boolean'},
        {name:'contentApproved', type:'boolean'},
        {name:'contentStatus', type:'string'},
        {name:'lastContentApprovalStatusUpdated', type:'number',convert: buildDate},
        {name:'imageName', type:'string'},
        {name:'created', type:'number',convert: buildDate},
        {name:'lastUpdated', type:'number',convert: buildDate},
        {name:'searchRanking', type:'int'},
        {name:'commissionPercent', type:'number'},
        {name:'restaurantUpdates', type:'auto'}
    ],
    hasMany:{
        model:'AD.model.NotificationOptions',
        name:'notificationOptions'
    }
});

