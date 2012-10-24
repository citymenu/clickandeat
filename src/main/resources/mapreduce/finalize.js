function finalize(key, value) {
    if( value.restaurant ) {
        value.restaurant.menu = null; // Clear menu object as we don't need it
    }
    return value;
}