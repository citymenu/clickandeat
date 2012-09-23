// Ajax controller
$(document).ajaxStart(function(){
    $.fancybox.showLoading();
});

$(document).ajaxComplete(function(){
    $.fancybox.hideLoading();
});

var locationHasBeenValid = false;
var locationValid = true;

// Initialize page
$(document).ready(function(){
    $('#searchbutton').click(function(){
        var location = $('#loc').val();
        checkLocationValid(location);
//        if( locationValid ) {
            validateLocation(location);
//        }
    });

    $('#loc').focusout(function(){
        checkLocationValid($(this).val());
    });

    $('#loc').keyup(function(){
        if( !locationValid || locationHasBeenValid ) {
            checkLocationValid($(this).val());
        }
    });
});

// Validates the location field
function checkLocationValid(location) {
    if( !location || location.search(/.*[0-9]{5}.*/) == -1 ) {
        if( locationValid == true ) {
            $('#validation-error').remove();
            $('#validation-wrapper').append(('<span id=\'validation-error\'>{0}</span>').format(getLabel('search.location-invalid')));
            locationValid = false;
        }
    } else {
        $('#validation-error').remove();
        locationHasBeenValid = true;
        locationValid = true;
    }
}

// Checks user search input
function validateLocation(location) {
    $.post( ctx+'/validateLocation.ajax', { loc: location },
        function( data ) {
            if( data.success ) {
                if( data.valid == false ) {
                    checkLocationValid(location);
                } else if( data.exactMatch == true ) {
                    window.location.href = ctx + '/findRestaurant.html';
                }
                else {
                    onResults(data.locations);
                }
            } else {
                alert('success:' + data.success);
            }
        }
    );
}

// Handler for multiple matches found
function onResults(locations) {
    if(locations.length == 0 ) {
        alert('Match not found');
    }
    else {
        showMatchingLocations(locations);
    }
}


// Handler for multiple results found
function showMatchingLocations(locations) {

    var matchHeader = '<h3>More than one matching address found.</h3>';
    var matchSubHeader = ('<div class=\'dialog-subtitle\'>{0}</div>').format('Is it one of the following?');

    var locationsMap = {};

    var matchEntries = '';
    var index = 0;
    locations.forEach(function(location){
        locationsMap[index] = location;
        matchEntries += ('<div class=\'location-match\'>&gt&gt <a class=\'location-link\' id=\'match_{0}\'>{1}</a></div>').format(index++, unescapeQuotes(location));
    });

    locations[index] = 'CANCEL';
    matchEntries += ('<div class=\'location-match\'>&gt&gt <a class=\'location-link\' id=\'match_{0}\'>{1}</a></div>').format(index,'None of the above');

    var matchHeader = ('<div class=\'dialog-header\'>{0}</div>').format(matchHeader);
    var matchBody = ('<div class=\'dialog-body\'>{0}{1}</div>').format(matchSubHeader,matchEntries);
    var matchFooter = ('<div class=\'dialog-footer\'></div>');
    var matchContainer = ('<div class=\'dialog-wrapper\'>{0}{1}{2}</div>').format(matchHeader,matchBody,matchFooter);

    // Show the dialog
    $.fancybox.open({
        type: 'html',
        content: matchContainer,
        modal:false,
        openEffect:'none',
        closeEffect:'none'
    });

    // Add click events to all of the divs
    $('.location-link').click(function(){
        var id = $(this).attr('id');
        var index = id.split("_")[1];
        var location = locations[index];
        if( location == 'CANCEL') {
            $.fancybox.close(true);
        } else {
            $.fancybox.close(true);
            validateLocation(location);
        }
    });

}

