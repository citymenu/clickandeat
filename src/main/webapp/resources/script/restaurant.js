// Top positions for fixed elements;
var launchtop;
var ordertop;

// Initialize scrolling options for menu launch and order panel launch
$(document).ready(function(){

    var index = 1;
    $('.menu-item-wrapper').each(function(){
        if( index++ % 2 == 0 ) {
            $(this).addClass('even');
        }
    });

    launchtop = $('#menu-launch-wrapper').offset().top - parseFloat($('#menu-launch-wrapper').css('marginTop').replace(/auto/, 0));
    ordertop = $('#order-wrapper').offset().top - parseFloat($('#order-wrapper').css('marginTop').replace(/auto/, 0));
    updateLaunchPos();
    updateOrderPanelPos();
    $(window).scroll(function (event) {
        updateLaunchPos();
        updateOrderPanelPos();
    });
});

// Runs after the order panel is built
function onAfterBuildOrder(order,config) {
    $('#order-wrapper').removeClass('fixed');
    ordertop = $('#order-wrapper').offset().top - parseFloat($('#order-wrapper').css('marginTop').replace(/auto/, 0));
    updateOrderPanelPos();
}


// Updates the position of the menu launch object
function updateLaunchPos() {
    var y = $(this).scrollTop();
    if( y > launchtop ) {
        $('#menu-launch-wrapper').addClass('fixed');
    } else {
        $('#menu-launch-wrapper').removeClass('fixed');
    }
}

// Set the order panel fixed or floating
function updateOrderPanelPos() {
    var y = $(this).scrollTop();
    var orderheight = $('#order-wrapper').height() + 10;
    var contenttop = $('.content-wrapper').offset().top;
    var contentheight = $('.content-wrapper').height();
    var contentbottom = contenttop + contentheight;
    var orderbottom = orderheight + y;

    if( orderbottom >= contentbottom ) {
        var newtop = contentbottom - orderheight - ordertop;
        $('#order-wrapper').css('top',(newtop < 0? 20: newtop));
        $('#order-wrapper').removeClass('fixed');
    } else if (y >= ordertop - 20 ) {
        $('#order-wrapper').css('top',20);
        $('#order-wrapper').addClass('fixed');
    } else {
        $('#order-wrapper').css('top',20);
        $('#order-wrapper').removeClass('fixed');
    }
}

function jump(category) {
    $.scrollTo('#' + category, 0, {offset: -10});
}

// Build directions map to restaurant
function showDirections(coordinates) {

    var header = ('<div class=\'dialog-header\'><h2>{0}</h2></div>').format(getLabel('restaurant.directions'));
    var content = ('<div class=\'dialog-content\'><div id=\'map_canvas\'></div></div>');
    var container = ('<div class=\'dialog-wrapper\'>{0}{1}</div>').format(header,content);

    $.fancybox.open({
        type: 'html',
        content: container,
        modal:false,
        autoSize:false,
        height: 375,
        width: 500,
        openEffect:'none',
        closeEffect:'none'
    });

    var mapOptions = {
        center: new google.maps.LatLng(-34.397, 150.644),
        zoom: 8,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map(document.getElementById("map_canvas"),mapOptions);
}
