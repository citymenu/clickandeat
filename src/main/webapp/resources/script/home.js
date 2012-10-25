$(document).ready(function(){

    // Add carousel
    $(".carousel-items").jCarouselLite({
        auto: 2000,
        speed: 1200,
        circular:true,
        visible:4

    });


    // If error in model, show location warning
    if( notfound == 'true' ) {
        $('#search-warning').show();
    }

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
                    window.location.href = ctx + '/app/' + getLabel('url.find-takeaway') + '/session/loc';
                }
                else {
                    $('#search-warning').show();
                }
            }
        );
    }
}


