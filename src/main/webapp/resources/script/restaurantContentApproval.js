

// Here we are going to hide the your order panel etc and present the buttons to approve
$(document).ready(function(){
    // Hide the Your Order panel
    $('.menu-right').hide();

    // Build the new buttons to approve or reject the content
     var approveButton = "<li class='unselectable'><a href='#' onclick='javascript:approveContent()'>"+getLabel('button.approve-content')+"</a></li>";
     var rejectButton = "<li class='unselectable'><a href='#' onclick='javascript:rejectContent()'>"+getLabel('button.reject-content')+"</a></li>";

    // Remove the current links from the action bar
    //$('.navigation-links').replaceWith( '<ul>'+approveButton + rejectButton +'</ul>');
    $('.navigation-links').html( '<ul>'+approveButton + rejectButton +'</ul>');
    // Add the approve/reject content buttons to the action bar


});


function rejectContentOLD() {
   $.post( ctx+'/workflow/contentRejected.html?contentStatus='+contentStatus+'&restaurantId='+restaurantId+'&mgn=' + (Math.random() * 99999999),
       function( data ) {
           if( data.success ) {
                buildOrder(data.order);
           } else {
                alert('success:' + data.success);
           }
       }
   );

}

function approveContent() {

    alert("They have clicked on approve new no AJAX");

    location.href = ctx + '/approval/restaurant/contentApproved.html?restaurantId='+restaurantId+'&mgn=' + (Math.random() * 99999999);

}

// Reject content. Present a bot to enter the reasons why the content is being rejected
function rejectContent() {

alert("NEWWW");

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
        $.post( ctx+'/order/updateAdditionalInstructions.ajax', {
            orderId: currentOrder.orderId,
            additionalInstructions: $('#instructions').val()
        },function( data ) {
                $.fancybox.hideLoading();
                $.fancybox.close(true);
                if( data.success ) {
                    buildOrder(data.order);
                } else {
                    alert('success:' + data.success);
                }
            }
        );
    });
}
