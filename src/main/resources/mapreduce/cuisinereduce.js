function (key, values) {
    var result = {id:key, cuisines: []};
    var cuisines = [];
    for (var i = 0; i < values.length; i++) {
        for( var j = 0; j < values[i].cuisines.length; j++ ) {
            var cuisine = values[i].cuisines[j];
            if( result.cuisines.indexOf(cuisine) == -1 ) {
                result.cuisines.push(cuisine);
            }
        }
    }
    result.cuisines.sort();
    return result;
}