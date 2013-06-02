function map() {
    if( this.address.postCode != null && this.address.postCode != '' ) {
        emit(this.address.postCode.trim(),{cuisines: this.cuisines});
    }
    if( this.address.town != null && this.address.town != '' ) {
        emit(this.address.town.trim(),{cuisines: this.cuisines});
    }
}