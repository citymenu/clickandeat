// Executes search function
function search(fieldId) {
    var location = $('#' + fieldId).val();
    if( location != '' ) {
        $.post( ctx+'/validateLocation.ajax', { loc: location },
            function( data ) {
                if( data.success ) {
                    var address = unescapeQuotes(data.address);
                    window.location.href = ctx + '/findRestaurant.html';
                }
                else {
                    alert('Do somthing here');
                }
            }
        );
    }
}
