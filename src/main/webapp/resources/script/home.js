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

    // Add banner carousel
    $("#bannercarousel").jCarouselLite({
        auto: 5000,
        speed: 1200,
        vertical: false,
        circular:true,
        visible:2
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

    // Add restaurant links
    $('.restaurant-link').click(function(){
        location.href = $(this).attr('url');
    });

    // Fade through the three speech options
    $('#speech1').delay(7000).fadeOut(500);
    $('#speech2').delay(7500).fadeIn(500).delay(7000).fadeOut(500);
    $('#speech3').delay(15500).fadeIn(500);


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

