// Default Ext blank image override
Ext.BLANK_IMAGE_URL = ctx + '/resources/images/s.gif';

// Extend Ajax timeout
Ext.Ajax.timeout = 1000 * 60 * 2;

// Cookie provider
var cp = Ext.create('Ext.state.CookieProvider', {
    expires: new Date(new Date().getTime()+(1000*60*60*24)) // 1 day
});

Ext.state.Manager.setProvider(cp);

/** Fix scrolling bug **/
Ext.override(Ext.grid.Scroller, {
    onAdded: function() {
        this.callParent(arguments);
        var me = this;
        if (me.scrollEl) {
            me.mun(me.scrollEl, 'scroll', me.onElScroll, me);
            me.mon(me.scrollEl, 'scroll', me.onElScroll, me);
        }
    }
});

/* Global ajax error handler */
Ext.Ajax.on('requestexception', 
	function(conn,response,options) {
		var status = response.status;
		if(status == '401') {
			location.reload(true);
			System.exit(0);
		} else if(status == '403') {
			location.replace(ctx + '/accessDenied.html');
			System.exit(0);
		}
	}
);

/* Fix loadmask bug */
Ext.override(Ext.view.AbstractView, {
	onMaskBeforeShow: function() {
		if (!this.el.isVisible(true)) return false;
		this.callOverridden(arguments);
	}
});
