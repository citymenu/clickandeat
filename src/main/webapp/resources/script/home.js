$(document).ready(function(){

    $('#loc').focus();

    $('#loc').keydown(function(event){
        if( event.keyCode == 13 ) {
            search('loc');
        }
    });

    $('.location-button').click(function(){
        search('loc');
    });

    // Enable Google autocomplete
    var input = document.getElementById('loc');
    var options = {
      types: ['geocode'],
      componentRestrictions: {country: country}
    };
    autocomplete = new google.maps.places.Autocomplete(input, options);

});

