// Ajax controller
$(document).ajaxStart(function(){
    $.fancybox.showLoading();
});

$(document).ajaxComplete(function(){
    $.fancybox.hideLoading();
});

// Initialize page
$(document).ready(function(){
    $('#searchbutton').click(function(){
        var location = $('#loc').val();
        if( location && location != '' ) {
            validateLocation(location);
        }
    })
});

// Checks user search input
function validateLocation(location) {
    $.post( ctx+'/validateLocation.ajax', { loc: location },
        function( data ) {
            if( data.success ) {
                onResults(data.locations);
            } else {
                alert('success:' + data.success);
            }
        }
    );
}

// Handler for multiple matches found
function onResults(matches) {
    var validMatches = [];
    matches.forEach(function(match){
        if(!match.radiusInvalid) {
            validMatches.push(match);
        }
    });
    if(validMatches.length == 0 ) {
        alert('Match not found');
    }
    else if( validMatches.length == 1 ) {
        window.location.href = ctx + '/findRestaurant.html?loc=' + validMatches[0].address;
    }
    else {
        showMatches(validMatches);
    }
}


// Handler for multiple results found
function showMatches(matches) {

    var matchHeader = '<h3>More than one matching address found.</h3>';
    var matchSubHeader = ('<div class=\'dialog-subtitle\'>{0}</div>').format('Is it one of the following?');

    var matchEntries = '';
    matches.forEach(function(match){
        matchEntries += ('<div class=\'location-match\'>{0}</div>').format(match.formattedAddress);
    });

    matchEntries += ('<div class=\'location-match\'>{0}</div>').format('None of the above');
    var matchBody = ('<div class=\'dialog-body\'>{0}{1}</div>').format(matchSubHeader,matchEntries);
    var matchContainer = ('<div class=\'dialog-wrapper\'>{0}{1}</div>').format(matchHeader,matchBody);

    // Show the dialog
    $.fancybox.open({
        type: 'html',
        content: matchContainer,
        modal:false,
        openEffect:'none',
        closeEffect:'none'
    });

}

