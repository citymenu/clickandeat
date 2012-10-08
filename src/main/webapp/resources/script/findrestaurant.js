var ignoreClosed = false;
var cuisine = '';

// Initialize scrolling options for menu launch and order panel launch
$(document).ready(function(){

    // Add click handler for location change link
    $('#changeLocation').click(function(){
        var locationField = ('<input class=\'input-location\' id=\'loc\' placeholder\'{0}\'/>').format(getLabel('search.watermark'));
        var searchButton = ('<input class=\'searchbutton\' type=\'button\' id=\'searchbutton\' value=\'{0}\'/>').format(getLabel('button.search'));
        var locationEdit = ('<div class=\'search-location-edit\'>{0} {1}</div>').format(locationField,searchButton);
        $('.location-warning').remove();
        $('.search-location').remove();
        $('.search-location-results').remove();
        $('.search-location-edit').append(locationEdit);
        $('#locationedit').focus();

        // Add event handlers
        $('#loc').keydown(function(event){
            if( event.keyCode == 13 ) {
                search('loc');
            }
        });

        $('#searchbutton').click(function(){
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

    //Add change handler for checkbox
    $('#ignore-closed').change(function(){
        ignoreClosed = ('checked' == $('#ignore-closed').attr('checked'));
        filterRestaurants();
    });

    // Change handler for select box
    $('#cuisine-select').change(function(){
        cuisine = $('#cuisine-select').val();
        filterRestaurants();
    });
});

// Update visible restaurants
function filterRestaurants() {
    $('.search-result-wrapper').each(function(index,element){
        var open = $(this).attr('isOpen');
        var cuisineSummary = $(this).attr('cuisines');
        if( open == 'false' && ignoreClosed == true ) {
            $(this).hide();
        } else if( cuisine != '' && cuisineSummary.indexOf(cuisine) == -1 ) {
            $(this).hide();
        } else {
            $(this).show();
        }
    });
}

// Override order config
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: true,
        showBuildOrderLink: true,
        allowRemoveItems: true,
        allowUpdateFreeItem: true,
        enableCheckoutButton: true,
        showDiscountInformation: true,
        showAdditionalInformation: true
    };
    return config;
}