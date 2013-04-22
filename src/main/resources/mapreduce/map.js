function map() {

    var include = false;
    var distanceToLocation = 0;

    // If a geolocation is specified, check if the restaurant will deliver (collection only we can bypass here)
    if( address ) {

        // Get the distance from the restaurant to the search location
        var distance = getDistance(this.address.location[0],this.address.location[1]);
        distanceToLocation = distance;

        // First check delivery by postcode
        for( var i = 0; i < this.deliveryOptions.areasDeliveredTo.length; i++ ) {
            var area = this.deliveryOptions.areasDeliveredTo[i].toUpperCase();
            if( address.indexOf(area) != -1 ) {
                include = true;
            }
        }

        // Default to include for collection only
        if( this.deliveryOptions.collectionOnly ) {
            include = true;
        } else {
            var deliveryRadius = this.deliveryOptions.deliveryRadiusInKilometres || 0;
            if( distanceToLocation  < deliveryRadius + radius ) {
                include = true;
            }
        }
    } else {
        include = true; // No address, include everything
    }

    // If the restaurant is included, emit if there is no cuisine selected or it matches the cuisine
    if( include ) {

        // If a cuisine is specified, do not show restaurant without that cuisine
        if( cuisine == null || this.cuisines.indexOf(cuisine) != -1 ) {
            emit(this.restaurantId, {restaurant:this, distance:distanceToLocation, count:1});
        }

        // Emit all other cuisines
        for (var i = 0; i < this.cuisines.length; i++) {
            emit(this.cuisines[i], {restaurant:null, count:1});
        }
    }
}


/**
 * Returns the distance between two locations in kilometres
 */
function getDistance( lat2, lon2 ) {
    var R = 6378.137; // Radius of the earth in km
    var dLat = toRad(lat2-lat1);
    var dLon = toRad(lon2-lon1);
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
            Math.sin(dLon/2) * Math.sin(dLon/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c; // Distance in km
    return d;
}

function toRad(num) {
    return num * Math.PI / 180;
}

