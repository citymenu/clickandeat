function () {
    for (var i = 0; i < this.cuisines.length; i++) {
        emit(this.cuisines[i], 1);
    }
}