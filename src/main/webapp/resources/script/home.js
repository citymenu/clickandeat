$(document).ready(function(){

    $('#loc').keydown(function(event){
        if( event.keyCode == 13 ) {
            search();
        }
    });

    $('#searchbutton').click(function(){
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


function search() {
    var location = $('#loc').val();
    if( location != '' ) {
        $.post( ctx+'/validateLocation.ajax', { loc: location },
            function( data ) {
                if( data.success ) {
                    var address = unescapeQuotes(data.address);
                    window.location.href = ctx + '/findRestaurant.html';
                }
                else {
                    showNotFound();
                }
            }
        );
    }
}

function showNotFound() {
    alert("Location not found");
}

