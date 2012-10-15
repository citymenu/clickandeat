// Top positions for fixed elements;
var launchtop;
var ordertop;
var orderheight;

// Initialize scrolling options for menu launch and order panel launch
$(document).ready(function(){

    ensureHeight();

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
    ensureHeight();
    $('#order-wrapper').removeClass('fixed');
    $('#order-wrapper').css('top',20);
    ordertop = $('#order-wrapper').offset().top - parseFloat($('#order-wrapper').css('marginTop').replace(/auto/, 0));
    updateOrderPanelPos();
}

// Runs after the delivery panel is shown
function onBuildDeliveryEdit() {
    ensureHeight();
}

// Update the height of the menu right panel to ensure it can fit the order panel
function ensureHeight() {
    orderheight = $('#order-wrapper').outerHeight();
    $('.menu-right').css('min-height',orderheight);
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
    var orderpaddingtop = parseInt($('.menu-right').css('padding-top').replace('px',''));
    var orderheight = $('#order-wrapper').outerHeight() + orderpaddingtop;
    var orderbottom = orderheight + y;

    var contenttop = $('.menu-center').offset().top;
    var contentheight = $('.menu-center').outerHeight();
    var contentbottom = contenttop + contentheight;

    if( orderbottom >= contentbottom ) {
        var newtop = contentbottom - orderheight - ordertop + 40;
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


// Show all opening times for the restaurant
function showAllOpeningTimes() {
    $.post( ctx+'/restaurant/getOpeningTimes.ajax', { restaurantId: restaurantId },
        function( data ) {
            if( data.success ) {
                var header = ('<div class=\'dialog-header\'><h2>{0}</h2></div>').format(getLabel('restaurant.all-opening-times'));
                var table = '<div class=\'opening-time-container\'>';
                for( var key in data.openingTimes ) {
                    table += ('<div class=\'opening-time\'><div class=\'day-of-week\'>{0}:</div><div class=\'opening-time-detail\'>{1}</div></div>').format(key, data.openingTimes[key]);
                }
                table += '</div>';
                var content = ('<div class=\'dialog-content\'>{0}</div>').format(table);
                var container = ('<div class=\'dialog-container\'>{0}{1}</div>').format(header,content);

                $.fancybox.open({
                    type: 'html',
                    content: container,
                    modal:false,
                    autoSize:false,
                    width: 375,
                    autoHeight:true,
                    openEffect:'none',
                    closeEffect:'none'
                });
            }
        }
    );

}


function jump(category) {
    $.scrollTo('#' + category, 0, {offset: -10});
}
