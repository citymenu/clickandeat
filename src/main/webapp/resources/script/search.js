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
                onResults(location, data.locations);
            } else {
                alert('success:' + data.success);
            }
        }
    );
}

// Handler for multiple matches found
function onResults(location, matches) {
    if(matches.length == 0 ) {
        alert('Match not found');
    }
    else if( matches.length == 1 ) {
        window.location.href = ctx + '/findRestaurant.html?loc=' + encodeURI(location);
    }
    else {
        showMatches(matches);
    }
}


// Handler for multiple results found
function showMatches(matches) {

    var matchHeader = '<h3>More than one matching address found.</h3>';
    var matchSubHeader = ('<div class=\'dialog-subtitle\'>{0}</div>').format('Is it one of the following?');

    var locations = {};

    var matchEntries = '';
    var index = 0;
    matches.forEach(function(match){
        locations[index] = unescapeQuotes(match.address);
        matchEntries += ('<div class=\'location-match\'>&gt&gt <a class=\'location-link\' id=\'match_{0}\'>{1}</a></div>').format(index++, unescapeQuotes(match.displayAddress));
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

