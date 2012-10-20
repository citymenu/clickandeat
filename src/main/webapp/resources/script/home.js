$(document).ready(function(){

    $('#loc').watermark(watermark);

    $('#loc').focus();

    $('#loc').keydown(function(event){
        if( event.keyCode == 13 ) {
            search();
        }
    });

    $('.location-button').click(function(){
        search();
    });

    $('#madrid').click(function() {
        $('#loc').val('Madrid');
        search();
    });

    $('#barcelona').click(function() {
        $('#loc').val('Barcelona');
        search();
    });

    $('#london').click(function() {
        $('#loc').val('London');
        search();
    });

    // Enable Google autocomplete
    var input = document.getElementById('loc');
    var options = {
      types: ['geocode'],
      componentRestrictions: {country: country}
    };
    autocomplete = new google.maps.places.Autocomplete(input, options);

});

// Executes search function
function search() {
    var location = $('#loc').val();
    if( location != '' ) {
        $('#search-warning').hide();
        $.fancybox.showLoading(getLabel('ajax.checking-your-location'));
        $.post( ctx+'/validateLocation.ajax', { loc: location },
            function( data ) {
                $.fancybox.hideLoading();
                if( data.success ) {
                    var address = unescapeQuotes(data.address);
                    $.fancybox.showLoading(getLabel('ajax.finding-restaurants'));
                    window.location.href = ctx + '/findRestaurant.html';
                }
                else {
                    $('#search-warning').show();
                }
            }
        );
    }
}


