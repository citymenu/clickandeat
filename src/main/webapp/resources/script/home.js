$(document).ready(function(){

    $('#loc').watermark(watermark,{
        useNative: false,
    });

    $('#loc').typeahead({
        source: function (query, process) {
            return $.post( ctx + '/locationLookup.html', { query: query }, function (data) {
                return process(data.options);
            });
        }
    });

    $('#loc').keydown(function(event){
        if( event.keyCode == 13 ) {
            search();
        }
    });

    $('#searchbutton').click(function(){
        search();
    });
});


function search() {
    var location = $('#loc').val();
    if( location == '' ) {
        alert('Enter a location');
    } else {
        $.post( ctx+'/validateLocation.ajax', { loc: location },
            function( data ) {
                if( data.success ) {
                    window.location.href = ctx + '/findRestaurant.html';
                }
                else {
                    alert("Location not found");
                }
            }
        );
    }
}


