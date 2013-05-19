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
        var searchButton = ('<div class=\'search-small\'><div class=\'search-button-text-small\'>{0}</div></div>').format(getLabel('button.search'));
        var locationTable = ("<table width=\'390\'><tr valign=\'middle\'><td width=\'290\'>{0}</td><td width=\'100\'>{1}</td></tr></table").format(locationField,searchButton);
        var locationEdit = ('<div class=\'search-location-edit\'>{0}</div>').format(locationTable);
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

        $('.search-small').click(function(){
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
    $('#ignore-closed').removeAttr('checked');
    $('#ignore-closed').change(function(){
        ignoreClosed = ('checked' == $('#ignore-closed').attr('checked'));
        filterRestaurants();
        if(ignoreClosed) {
            $('#filterclosed').addClass('selectedcuisine');
        } else {
            $('#filterclosed').removeClass('selectedcuisine');
        }

    });

    // Change handler for radio
    $('input[name="cuisine"]').change(function(){
        var cuisine = $('input:radio[name=cuisine]:checked').val();
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

    // Add hover to results
    $('.result').hover(
        function() {
            $(this).addClass('result-hover');
        },
        function() {
            $(this).removeClass('result-hover');
        }
    );

    // Add url links to all restaurant buttons
    $('div[type="link"]').click(function(){
        location.href = $(this).attr('url');
    });

});



// Update visible restaurants
function filterRestaurants() {

    var displayPhoneOnlyMessage = false;

    $('.result-wrapper').each(function(index,element){
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
        $('.phone-orders-wrapper').show();
    } else {
        $('.phone-orders-wrapper').hide();
    }
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

// Update current location
function locationEdit() {

    var locationField = ('<input class=\'input-location\' id=\'locedit\' placeholder=\'\'/>').format(getLabel('search.watermark'));
    var searchButton = ('<div class=\'search-small\'><div class=\'search-button-text-small\'>{0}</div></div>').format(getLabel('button.search'));
    var locationTable = ("<table width=\'390\'><tr valign=\'middle\'><td width=\'290\'>{0}</td><td width=\'100\'>{1}</td></tr></table").format(locationField,searchButton);
    var locationEdit = ('<div class=\'search-location-edit\'>{0}</div>').format(locationTable);

    var header = ('<div class=\'dialog-header\'><h2>{0}</h2></div>').format(getLabel('location.enter-your-location'));
    var content = ('<div class=\'dialog-content\'>{0}</div>').format(locationTable);
    var container = ('<div class=\'dialog-container\'>{0}{1}</div>').format(header,content);

    $.fancybox.open({
        type: 'html',
        content: container,
        modal:false,
        autoSize:false,
        autoWidth:true,
        height:145,
        openEffect:'none',
        closeEffect:'none'
    });

    $('#locedit').attr('placeholder','');
    $('#locedit').watermark(watermark);
    $('#locedit').focus();

    // Add event handlers
    $('#locedit').keydown(function(event){
        if( event.keyCode == 13 ) {
            locationSearch();
        }
    });

    $('.search-small').click(function(){
        locationSearch();
    });

    // Enable Google autocomplete
    var input = document.getElementById('locedit');
    var options = {
      types: ['geocode'],
      componentRestrictions: {country: country}
    };
    autocomplete = new google.maps.places.Autocomplete(input, options);
}

// Update order location
function locationSearch() {
    var location = $('#locedit').val();
    if( location != '' ) {
        $.fancybox.showLoading(getLabel('ajax.checking-your-location'));
        $.post( ctx+'/validateLocation.ajax', { loc: location },
            function( data ) {
                $.fancybox.hideLoading();
                if( data.success ) {
                    $.fancybox.close(true);
                    onAfterLocationUpdate();
                }
            }
        );
    }
}

// Runs after location is updated
function onAfterLocationUpdate() {
    $.fancybox.showLoading(getLabel('ajax.finding-restaurants'));
    location.href = ctx + '/app/' + getLabel('url.find-takeaway') + '/session/loc';
}