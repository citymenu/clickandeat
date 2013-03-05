$(document).ready(function(){

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

    // Add carousel
    $("#carousel").jCarouselLite({
        auto: 5000,
        speed: 1200,
        vertical: true,
        circular:true,
        visible:2
    })
    .hover(function() {
        paused = true;
    }, function() {
        paused = false;
    });

    $('.search-button').click(function(){
        search();
    });

    $('#loc').watermark(watermark);

    $('#loc').focus();

    $('#loc').keydown(function(event){
        if( event.keyCode == 13 ) {
            search();
        }
    });

});

// Executes search function
function search() {
    var location = $('#loc').val();
    if( location != '' ) {
        $.fancybox.showLoading(getLabel('ajax.checking-your-location'));
        $.post( ctx+'/validateLocation.ajax', { loc: location },
            function( data ) {
                $.fancybox.hideLoading();
                if( data.success ) {
                    var address = unescapeQuotes(data.address);
                    $.fancybox.showLoading(getLabel('ajax.finding-restaurants'));
                    window.location.href = ctx + '/app/' + getLabel('url.find-takeaway') + '/session/loc';
                }
            }
        );
    }
}


