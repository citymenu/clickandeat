function map() {
    if( this.address.town != '' ) {
        emit(this.address.town.trim(),{cuisines: this.cuisines});
    }
}