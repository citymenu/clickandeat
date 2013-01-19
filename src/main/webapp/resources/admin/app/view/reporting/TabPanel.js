Ext.define('AD.view.reporting.TabPanel' ,{
    extend:'Ext.tab.Panel',
    alias:'widget.reportingtab',
    id:'reportingtab',
    layout:'fit',
    autoScroll:true,
    stateId:'reportingtabpanel',

    initComponent: function() {
        this.items = [{
            xtype:'ordersummary'
        }];

        this.callParent(arguments);
    }

});