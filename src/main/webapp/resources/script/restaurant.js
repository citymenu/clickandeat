// Top positions for fixed elements;
var launchtop;
var ordertop;

// Initialize scrolling options for menu launch and order panel launch
$(document).ready(function(){
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
        $('#order-wrapper').css('top',(newtop < 0? 0: newtop));
        $('#order-wrapper').removeClass('fixed');
    } else if (y >= ordertop ) {
        $('#order-wrapper').css('top',0);
        $('#order-wrapper').addClass('fixed');
    } else {
        $('#order-wrapper').css('top',0);
        $('#order-wrapper').removeClass('fixed');
    }
}


function jump(category) {
    $.scrollTo('#' + category, 0, {offset: -10});
}