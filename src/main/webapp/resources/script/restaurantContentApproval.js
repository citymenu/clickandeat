

// Here we are going to hide the your order panel etc and present the buttons to approve or reject
$(document).ready(function(){
    // Hide the Your Order panel. We don't hide it anymore so that the restaurant owner can test the phone call.
    //$('.menu-right').hide();

    // Build the new buttons to approve or reject the content
     var approveButton = "<li class='unselectable'><a href='#' onclick='javascript:approveContent()'>"+getLabel('button.approve-content')+"</a></li>";
     var rejectButton = "<li class='unselectable'><a href='#' onclick='javascript:rejectContent()'>"+getLabel('button.reject-content')+"</a></li>";
     var testPhoneCallButton = "<li class='unselectable'><a href='#' onclick='javascript:testPhoneCall()'>"+getLabel('button.test-phone-call')+"</a></li>";


    // Remove the current links from the action bar
    $('.navigation-links').html( '<ul>'+approveButton + rejectButton + testPhoneCallButton +'</ul>');
});


function testPhoneCall() {
    location.href = ctx + '/approval/restaurant/testPhoneCall.html?mgn=' + (Math.random() * 99999999);
}

function approveContent() {
    location.href = ctx + '/approval/restaurant/contentApproved.html?restaurantId='+restaurantId+'&mgn=' + (Math.random() * 99999999);
}

// Reject content. Present a box to enter the reasons why the content is being rejected
function rejectContent() {

    var header = getLabel('content.approval.reject.title');
    var subheader = getLabel('content.approval.reject.help');
    var content = '<textarea id=\'reasons\'></textarea>';
    var buttons = ('<a id=\'rejectbutton\' class=\'order-button order-button-large unselectable\'>{0}</a>').format(getLabel('button.reject-content'));

    var container = ('<div class=\'dialog-container\'><div class=\'dialog-header\'><h2>{0}</h2></div><div class=\'dialog-subheader\'>{1}</div><div class=\'dialog-content\'>{2}</div><div class=\'dialog-footer\'><div class=\'dialog-buttons\'>{3}</div></div></div>')
        .format(header,subheader,content,buttons);

    $.fancybox.open({
        type: 'html',
        content: container,
        modal:false,
        autoSize:false,
        autoHeight: true,
        width: 500,
        openEffect:'none',
        closeEffect:'none'
    });

    $('#rejectbutton').click(function(){
        var rejectionReasons = $('#reasons').val();
        $.fancybox.showLoading();
        $.post( ctx+'/approval/restaurant/contentRejected.ajax', {
            restaurantId: restaurantId,
            rejectionReasons: $('#reasons').val()
        },function( data ) {
                $.fancybox.hideLoading();
                $.fancybox.close(true);
                if( data.success ) {
                    // Open the url that displays the message to the restaurant owned
                    location.href = ctx + '/approval/restaurant/contentRejected.html?restaurantId='+restaurantId+'&mgn=' + (Math.random() * 99999999);

                } else {
                    alert('success:' + data.success);
                }
            }
        );
    });
}
