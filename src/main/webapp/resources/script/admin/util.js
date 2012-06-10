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