$(document).ready(function(){
    $('#selectanotherbutton').button();
    $('#selectanotherbutton').click(function(){
        if( searchLocation == '' ) {
            location.href = ctx;
        } else {
            location.href = ctx + '/findRestaurant.html?loc=' + searchLocation;
        }
    });
});
