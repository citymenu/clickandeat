/* Reloads the store associated with a named list widget */
function reloadListStore(listWidgetName) {
	var store = Ext.widget(listWidgetName).store;
	store.loadPage(store.currentPage);
}

/* Formats a date from a numeric value */ 
function formatDate(value){
	return value? Ext.Date.format(new Date(value),'d/m/Y'): '';
}

/* Unescapes quotes */
function unescapeQuotes(str) {
    if( !str || str == '' ) {
        return str;
    } else {
        return str.replace(/###/g,"'").replace(/%%%/g,'"');
    }
}

/* Builds a date object */
function buildDate(val) {
    return new Date(val);
}

function booleanToString(val) {
    return val && val == true? 'Y':'N';
}

function renderNotificationCall(value, metaData, record) {
    var associatedData = record.getAssociatedData();
    var notificationOptions = associatedData.notificationOptions[0];
    return booleanToString(notificationOptions.receiveNotificationCall);
}

function renderSMSNotification(value, metaData, record) {
    var associatedData = record.getAssociatedData();
    var notificationOptions = associatedData.notificationOptions[0];
    return booleanToString(notificationOptions.receiveSMSNotification);
}


function renderOrderId(value, metaData, record) {
    var order = record.get('order');
    return order? order.orderId: '';
}

/* Converts a newline delimited string into an array */
function delimitedStringToArray(str,delim) {
    var arr = [];
    if( !str || str == '' ) {
        return arr;
    } else {
        str.split(delim).forEach(function(val){
            var unescaped = unescapeQuotes(val);
            var trimmed = $.trim(unescaped);
            if( trimmed && trimmed != '') {
                arr.push(unescaped);
            }
        });
        return arr;
    }
}

/* Converts <br> into newline on model init */
function replaceLineBreaks(value) {
    return value? unescapeQuotes(value.replace(/<br>/g,'\n')): null;
}

/* Converts <br> into newline on model init */
function replaceNewLines(value) {
    return value? value.replace(/\n/g,'<br>'): null;
}

/* Converts array into newline delimited string */
function arrayToString(value, record) {
    if( value && (value instanceof Array)) {
        return unescapeQuotes(value.join('\n'));
    } else {
        return value;
    }
}

// Parse out order status
function convertOrderStatus(value,record) {
    if( !value || value == '' ) {
        return value;
    } else {
        return value.replace('ORDER_STATUS_','').replace('_',' ');
    }
}

// Parse out order notification status
function convertOrderNotificationStatus(value,record) {
    if( !value || value == '' ) {
        return value;
    }
    else {
        return value.replace(/_/g,' ');
    }
}

// Converts a date to a month only date
function convertDateToMonth(value, record) {
    return value? Ext.Date.format(new Date(value),'Y/m'): '';
}

// plug European currency renderer into formatter
Ext.util.Format.Euro = function(v) {
	v = (Math.round((v-0)*100))/100;
	v = (v == Math.floor(v)) ? v + ".00" : ((v*10 == Math.floor(v*10)) ? v + "0" : v);
	return (v + ' &euro;').replace(/\./, ',');
};

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
			if (msgCt){ msgCt.destroy(); }
			msgCt = Ext.DomHelper.insertFirst(document.body, {id: 'msg-div'}, true);
			msgCt.setWidth(250);
           	msgCt.alignTo(target, 'tr-tr',[-15,15]);
           	var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 3));
       		var m = Ext.DomHelper.append(msgCt, {html:createBox(title, s, cls)}, true);
       		var d = delay? delay: 4;
       		m.slideIn('t',{easing:'easeOut'}).fadeOut({opacity:1,duration:1500,remove:false}).fadeOut({duration:250,remove:true});
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
