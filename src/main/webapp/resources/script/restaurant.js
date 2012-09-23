var launchtop;

$(document).ready(function(){
    launchtop = $('#menu-launch-wrapper').offset().top - parseFloat($('#menu-launch-wrapper').css('marginTop').replace(/auto/, 0));
    updateLaunchPos();
    $(window).scroll(function (event) {
        updateLaunchPos();
    });
});

function updateLaunchPos() {
    var y = $(this).scrollTop();
    if( y > launchtop ) {
        $('#menu-launch-wrapper').addClass('fixed');
    } else {
        $('#menu-launch-wrapper').removeClass('fixed');
    }
}

function jump(category) {
    $.scrollTo('#' + category, 0, {offset: -10});
}