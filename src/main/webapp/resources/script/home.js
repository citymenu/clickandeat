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
        auto: 2500,
        speed: 1200,
        vertical: false,
        circular:true,
        visible:2
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

    // Add restaurant links
    $('.restaurant-link').click(function(){
        location.href = $(this).attr('url');
    });

    // Fade through the three speech options
    $('#speech1').delay(7000).fadeOut(500);
    $('#speech2').delay(7500).fadeIn(500).delay(7000).fadeOut(500);
    $('#speech3').delay(15500).fadeIn(500);

    // Recommendations hover
    $('div[type="recommendation"]').hover(
        function() {
            $(this).addClass('restaurant-panel-hover');
        },
        function() {
            $(this).removeClass('restaurant-panel-hover');
        }
    );

    // Add url links to all restaurant buttons
    $('div[type="recommendation"]').click(function(){
        var url = $(this).attr('url');
        window.location = ctx + '/' + url;
    });

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

