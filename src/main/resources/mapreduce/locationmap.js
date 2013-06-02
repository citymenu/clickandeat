function map() {
    if( this.address.town != null && this.address.town != '' ) {
        emit(this.address.town.trim(),1);
    }
}