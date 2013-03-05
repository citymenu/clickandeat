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

    // Add onclick handler to change link
    $('#changeLocation').click(function(){
        $('#locationdisplay').hide();
        $('#locationedit').show();
    });
});

// Runs after the order panel is built
function onAfterBuildOrder(order,config) {
    ensureHeight();
    $('#order-wrapper').removeClass('fixed');
    $('#order-wrapper').css('top',0);
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
    launchheight = $('#menu-launch-wrapper').outerHeight();
    $('.menu-left').css('min-height',launchheight);
}

// Updates the position of the menu launch object
function updateLaunchPos() {
    var y = $(this).scrollTop();

    var launchpaddingtop = parseInt($('.menu-left').css('padding-top').replace('px',''));
    var launchheight = $('#menu-launch-wrapper').outerHeight() + launchpaddingtop;
    var launchbottom = launchheight + y + 20;

    var contenttop = $('.menu-center').offset().top;
    var contentheight = $('.menu-center').outerHeight();
    var contentbottom = contenttop + contentheight;

    if( launchbottom >= contentbottom ) {
        var newtop = contentbottom - launchheight - launchtop - 20;
        $('#menu-launch-wrapper').css('top',(newtop < 0? 0: newtop));
        $('#menu-launch-wrapper').removeClass('fixed');
    } else if (y >= launchtop) {
        $('#menu-launch-wrapper').css('top',0);
        $('#menu-launch-wrapper').addClass('fixed');
    } else {
        $('#menu-launch-wrapper').css('top',0);
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
    var contentbottom = contenttop + contentheight - 20;

    if( orderbottom >= contentbottom ) {
        var newtop = contentbottom - orderheight - ordertop + 20;
        $('#order-wrapper').css('top',(newtop < 0? 0: newtop));
        $('#order-wrapper').removeClass('fixed');
    } else if (y >= ordertop - 20 ) {
        $('#order-wrapper').css('top',20);
        $('#order-wrapper').addClass('fixed');
    } else {
        $('#order-wrapper').css('top',0);
        $('#order-wrapper').removeClass('fixed');
    }
}

// Show the telephone number for a restaurant
function showContactTelephone() {
    $.fancybox.open({
        href: ctx + '/app/contact/' + restaurantId + '/' + restaurantName,
        type:'iframe',
        width:320,
        autoHeight:true,
        modal:false,
        openEffect:'none',
        closeEffect:'none'
    });
}


// Show all opening times for the restaurant
function showAllOpeningTimes() {
    $.fancybox.showLoading();
    $.post( ctx+'/restaurant/getOpeningTimes.ajax', { restaurantId: restaurantId },
        function( data ) {
            $.fancybox.hideLoading();
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

// Show all delivery charges for the restaurant
function showAllDeliveryCharges() {
    $.fancybox.showLoading();
    $.post( ctx+'/restaurant/getDeliveryCharges.ajax', { restaurantId: restaurantId },
        function( data ) {
            $.fancybox.hideLoading();
            if( data.success ) {

                // Get free delivery values
                var standardDeliveryCharge = data.deliveryCharge? data.deliveryCharge: 0;
                var minimumOrderForDelivery = data.minimumOrderForDelivery? data.minimumOrderForDelivery: 0;
                var minimumOrderForFreeDelivery = data.minimumOrderForFreeDelivery && data.allowFreeDelivery == true? data.minimumOrderForFreeDelivery: 0;

                // Build map of delivery charges and minimum order values
                var deliveryCharges = {};
                var hasDeliveryCharge = false;
                var hasMinimumOrder = false;

                $.each( data.areaDeliveryCharges, function( key, arr ) {
                    deliveryCharges[key] = arr;
                    if( arr[0] != null ) {
                        hasDeliveryCharge = true;
                    }
                    if( arr[1] != null ) {
                        hasMinimumOrder = true;
                    }
                });

                // Build the standard content
                var standardContent = '';
                if( standardDeliveryCharge != 0 ) {
                    standardContent += ('<tr><td>{0}</td><td class=\'right\'>{1} <span class="euro">{2}</span></td></tr>')
                        .format(getLabel('restaurant.standard-delivery-charge'),standardDeliveryCharge.toFixed(2),ccy);
                }

                data.variableDeliveryCharges.forEach(function(variableDeliveryCharge){
                    standardContent += ('<tr><td>{0} {1} <span class="euro">{2}</span></td><td class=\'right\'>{3} <span class="euro">{4}</span></td></tr>')
                        .format(getLabel('restaurant.delivery-charge-for-orders-over'),variableDeliveryCharge.minimumOrderValue.toFixed(2),ccy,variableDeliveryCharge.deliveryCharge.toFixed(2),ccy);
                });

                if( minimumOrderForDelivery != 0 ) {
                    standardContent += ('<tr><td>{0}</td><td class=\'right\'>{1} <span class="euro">{2}</span></td></tr>')
                        .format(getLabel('restaurant.minimum-order-for-delivery'),minimumOrderForDelivery.toFixed(2),ccy);
                }
                if( minimumOrderForFreeDelivery != 0 ) {
                    standardContent += ('<tr><td>{0}</td><td class=\'right\'>{1} <span class="euro">{2}</span></td></tr>')
                        .format(getLabel('restaurant.minimum-order-for-free-delivery'),minimumOrderForFreeDelivery.toFixed(2),ccy);
                }

                if( standardContent != '' ) {
                    standardContent = '<div class=\'standard-delivery-details\'><table class=\'standard-delivery\'>' + standardContent + '</table></div>';
                }

                // Build the location delivery options
                var table = '<table class=\'delivery-details-table\'><thead><tr>';
                table += ('<th>{0}</th>').format(getLabel('restaurant.delivery-location'));
                if( hasDeliveryCharge ) {
                    table += ('<th>{0}</th>').format(getLabel('restaurant.delivery-charge'));
                }
                if( hasMinimumOrder ) {
                    table += ('<th>{0}</th>').format(getLabel('restaurant.minimum-order-value'));
                }
                table += '</tr></thead><tbody>';

                var rowIndex = 0;
                $.each( deliveryCharges, function( key, arr ) {
                    var areaDeliveryCharge = arr[0];
                    var areaMinimumOrderValue = arr[1];
                    var cls = ( rowIndex++ % 2 ) == 0? 'even': 'odd';
                    var row = ('<tr class=\'{0}\'><td>{1}</td>').format(cls,key);
                    if( hasDeliveryCharge ) {
                        row += ('<td class=\'right\'>{0} <span class=\'euro\'>{1}</span></td>').format(areaDeliveryCharge? areaDeliveryCharge.toFixed(2): standardDeliveryCharge.toFixed(2), ccy );
                    }
                    if( hasMinimumOrder ) {
                        row += ('<td class=\'right\'>{0} <span class=\'euro\'>{1}</span></td>').format(areaMinimumOrderValue? areaMinimumOrderValue.toFixed(2): minimumOrderForDelivery.toFixed(2), ccy );
                    }
                    row += '</tr>';
                    table += row;
                });

                table += '</tbody></table>';

                var header = ('<div class=\'dialog-header\'><h2>{0}</h2></div>').format(getLabel('restaurant.delivery-charges'));
                var content = ('<div class=\'dialog-content\'>{0}{1}</div>').format(standardContent,table);
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
