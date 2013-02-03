var gallery_photo_create_map_check = 0;

// Build directions map to restaurant
function showDirections(restaurantLat,restaurantLng,userLat,userLng,restaurantName) {

    var hasUserLocation = (userLat != null && userLng != null);
    var headerMessage = hasUserLocation? getLabel('restaurant.directions'): getLabel('restaurant.location');
    var header = ('<div class=\'dialog-header\'><h2>' + headerMessage + '</h2></div>').format(unescapeQuotes(restaurantName));
    var content = ('<div class=\'dialog-content\'><div id=\'map_canvas\'></div></div>');
    var container = ('<div class=\'dialog-wrapper\'>{0}{1}</div>').format(header,content);

    $.fancybox.open({
        type: 'html',
        content: container,
        modal:false,
        autoSize:true,
        openEffect:'none',
        closeEffect:'none'
    });

    // If user location is null, just show restaurant
    if( !hasUserLocation ) {

        var restaurantLatlng = new google.maps.LatLng(restaurantLat,restaurantLng);
        var mapOptions = {
            center: restaurantLatlng,
            zoom: 15,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        // Display map
        var map = new google.maps.Map(document.getElementById("map_canvas"),mapOptions);

        // Add restaurant marker
        var restaurantMarker = new google.maps.Marker({
            position: restaurantLatlng,
            map: map
        });

        return;
    }

    // Coordinates of user and restaurant
    var restaurantLatlng = new google.maps.LatLng(restaurantLat,restaurantLng);
    var userLatlng = new google.maps.LatLng(userLat,userLng);

    // Show both locations on map
    var north = Math.max(restaurantLng, userLng);
    var south = Math.min(restaurantLng, userLng);
    var west = Math.min(restaurantLat, userLat);
    var east = Math.max(restaurantLat, userLat);

    // Calculate bounding rectangle
    var southWest = new google.maps.LatLng(west,south);
    var northEast = new google.maps.LatLng(east,north);
    var bounds = new google.maps.LatLngBounds(southWest,northEast);

    // Get map center
    var centerLat = (west + east) / 2;
    var centerLng = (north + south) / 2;
    var centerLatlng = new google.maps.LatLng(centerLat,centerLng);

    // Directions service and renderer
    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay = new google.maps.DirectionsRenderer();
    directionsDisplay.suppressMarkers = true;

    var mapOptions = {
        center: centerLatlng,
        zoom: 15,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    // Display map
    var map = new google.maps.Map(document.getElementById("map_canvas"),mapOptions);
    directionsDisplay.setMap(map);
    map.fitBounds(bounds);

    // Routing request
    var request = {
        origin:userLatlng,
        destination:restaurantLatlng,
        travelMode: google.maps.TravelMode.DRIVING
    };

    // Route request
    directionsService.route(request, function(result, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setDirections(result);
        }
    });

}
