/* Reloads the store associated with a named list widget */
function reloadListStore(listWidgetName) {
	var store = Ext.widget(listWidgetName).store;
	store.loadPage(store.currentPage);

}

/* Formats a date from a numeric value */ 
function formatDate(value){
	return value? Ext.Date.format(new Date(value),'d/m/Y'): '';
}
