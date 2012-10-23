function (key, values) {
    var result = {restaurant:null, count: 0};
    for (var i = 0; i < values.length; i++) {
        result.restaurant = values[i].restaurant;
        result.count += values[i].count;
    }
    return result;
}