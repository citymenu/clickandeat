/* Reloads the store associated with a named list widget */
function reloadListStore(listWidgetName) {
	var store = Ext.widget(listWidgetName).store;
	store.loadPage(store.currentPage);

}

/* Formats a date from a numeric value */ 
function formatDate(value){
	return value? Ext.Date.format(new Date(value),'d/m/Y'): '';
}

/* Converts a newline delimited string into an array */
function delimitedStringToArray(str,delim) {
    var arr = [];
    if( !str || str == '' ) {
        return arr;
    } else {
        str.split(delim).forEach(function(val){
            arr.push(val);
        });
        return arr;
    }
}

//Messaging
Ext.message = function(){

	var msgCt;

    function createBox(t, s, cls){
        return ['<div class="msg">',
                '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
                '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><div class="' + cls + '">', t,
				'</div><span class="x-box-txt">', s, '</span></div></div></div>',
                '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
                '</div>'].join('');
    }
    return {
        msg : function(target, cls, title, format, delay){
			if (msgCt){ msgCt.remove(); }
			msgCt = Ext.DomHelper.insertFirst(document.body, {id: 'msg-div'}, true);
			msgCt.setWidth(250);
           	msgCt.alignTo(target, 'tr-tr',[-15,15]);
           	var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 2));
       		var m = Ext.DomHelper.append(msgCt, {html:createBox(title, s, cls)}, true);
       		var d = delay? delay: 4;
       		m.slideIn('t',{easing:'easeOut'}).fadeOut({opacity:1,duration:2000,remove:false}).fadeOut({duration:250,remove:true});
        },

        init : function(){
            var t = Ext.get('exttheme');
            if(!t){ // run locally?
                return;
            }
            var theme = Cookies.get('exttheme') || 'aero';
            if(theme){
                t.dom.value = theme;
                Ext.getBody().addClass('x-'+theme);
            }
            t.on('change', function(){
                Cookies.set('exttheme', t.getValue());
                setTimeout(function(){
                    window.location.reload();
                }, 250);
            });

            var lb = Ext.get('lib-bar');
            if(lb){
                lb.show();
            }
        }
    };
}();

Ext.onReady(Ext.message.init, Ext.message);

//Displays an error message
function showErrorMessage(target,title,msgtxt) {
	Ext.message.msg(target,'msg-box-title-error',title,msgtxt ? msgtxt: '',4);
}

// Displays an warning message
function showWarningMessage(target,title,msgtxt) {
	Ext.message.msg(target,'msg-box-title-warning',title,msgtxt ? msgtxt: '');
}

//Displays a success message
function showSuccessMessage(target,title,msgtxt) {
	Ext.message.msg(target,'msg-box-title-info',title,msgtxt ? msgtxt: '');
}
