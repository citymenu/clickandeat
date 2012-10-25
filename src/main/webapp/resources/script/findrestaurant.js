var ignoreClosed = false;
var cuisine = '';

// Initialize scrolling options for menu launch and order panel launch
$(document).ready(function(){

    // Hide the registered message bod
    $('#registered').hide();

    // Hide the invalid email address field
    $('.invalid-email').hide();

    // Add keydown handler to email field
    $('#email').keydown(function(){
        $('.invalid-email').hide();
    });

    // Add click handler for location change link
    $('#changeLocation').click(function(){
        var locationField = ('<input class=\'input-location\' id=\'loc\' placeholder=\'\'/>').format(getLabel('search.watermark'));
        var searchButton = ('<a class=\'search\'>{0}</a>').format(getLabel('button.search'));
        var locationEdit = ('<div class=\'search-location-edit\'>{0} {1}</div>').format(locationField,searchButton);
        $('.search-location').remove();
        $('.search-location-results').remove();
        $('.search-location-edit').append(locationEdit);
        $('#loc').attr('placeholder','');
        $('#loc').watermark(watermark);
        $('#loc').focus();

        // Add event handlers
        $('#loc').keydown(function(event){
            if( event.keyCode == 13 ) {
                search();
            }
        });

        $('.search').click(function(){
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

    //Add change handler for checkbox
    $('#ignore-closed').change(function(){
        ignoreClosed = ('checked' == $('#ignore-closed').attr('checked'));
        filterRestaurants();
    });

    // Change handler for select box
    $('#cuisine-select').change(function(){
        var cuisine = $('#cuisine-select').val();
        $.fancybox.showLoading(getLabel('ajax.finding-restaurants'));
        $.post( ctx+'/updateLocation.ajax', { loc: address, c: cuisine },
            function( data ) {
                if( address == null || address == '' ) {
                    window.location.href = ctx + '/app/' + getLabel('url.find-takeaway') + '/session/noloc';
                } else {
                    window.location.href = ctx + '/app/' + getLabel('url.find-takeaway') + '/session/loc';
                }
            }
        );
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
                    window.location.href = ctx + '/app/' + getLabel('url.find-takeaway') + '/session/loc';                }
                else {
                    $('.location-warning').remove();
                    $('.location-warning-wrapper').append(('<div class=\'location-warning\'>{0}</div>').format(getLabel('search.location-not-found')));
                }
            }
        );
    }
}

// Update visible restaurants
function filterRestaurants() {

    var displayPhoneOnlyMessage = false;

    $('.search-result-wrapper').each(function(index,element){
        var open = $(this).attr('isOpen');
        var cuisineSummary = $(this).attr('cuisines');
        if( open == 'false' && ignoreClosed == true ) {
            $(this).hide();
        } else if( cuisine != '' && cuisineSummary.indexOf(cuisine) == -1 ) {
            $(this).hide();
        } else {
            $(this).show();
            if($(this).attr('isPhoneOnly') == 'true') {
                displayPhoneOnlyMessage = true;
            }
        }
    });

    if( displayPhoneOnlyMessage ) {
        $('.phone-orders-only-wrapper').show();
    } else {
        $('.phone-orders-only-wrapper').hide();
    }
}

// Override order config
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: true,
        showBuildOrderLink: true,
        allowRemoveItems: true,
        allowUpdateFreeItem: true,
        enableCheckoutButton: true,
        enablePaymentButton: false,
        showDiscountInformation: true,
        showAdditionalInformation: true,
        displayAdditionalInformation:false
    };
    return config;
}

// Registers when no restaurants found
function register() {
    $('.invalid-email').hide();
    var regexp = checkoutRegexps.email;
    var email = $('#email').val();
    if( !regexp.test(email)) {
        $('.invalid-email').show();
    } else {
        $.fancybox.showLoading();
        $.post( ctx + '/register/registerCustomer.ajax', { email: email, discount: 10 },
            function( data ) {
                $.fancybox.hideLoading();
                $('#notregistered').hide();
                $('#registered').show();
            }
        );
    }
}
