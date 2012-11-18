function map() {
    if( this.address.town != '' ) {
        emit(this.address.town,{cuisines: this.cuisines});
    }
}