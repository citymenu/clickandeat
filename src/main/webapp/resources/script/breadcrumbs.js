function buildBreadcrumbs() {
    if( !breadcrumbs || breadcrumbs.length == 0) {
        return;
    }
    var keys = breadcrumbs.keys();
    for( var i = 0; i < keys.length; i++ ) {
        var key = keys[i];
        var value = breadcrumbs.getItem(key);
        if( value== '' ) {
            var breadcrumb = '<div class=\'breadcrumbitem\'>{0}</div>'.format(key);
            $('#breadcrumb').append(breadcrumb);
        } else {
            var breadcrumb = '<div class=\'breadcrumbitem\'><a href=\'{0}\'>{1}</a></div>'.format(value,key);
            $('#breadcrumb').append(breadcrumb);
        }
    }
}