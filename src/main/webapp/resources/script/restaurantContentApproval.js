

// Here we are going to hide the your order panel etc and present the buttons to approve
$(document).ready(function(){
    // Hide the Your Order panel
    $('.menu-right').hide();

    // Build the new buttons to approve or reject the content
     var approveButton = "<li class='unselectable'><a href='#' onclick='javascript:approveContentStatus()'>"+getLabel('button.approve-content')+"</a></li>";
     var rejectButton = "<li class='unselectable'><a href='#' onclick='javascript:rejectContentStatus()'>"+getLabel('button.reject-content')+"</a></li>";

    // Remove the current links from the action bar
    //$('.navigation-links').replaceWith( '<ul>'+approveButton + rejectButton +'</ul>');
    $('.navigation-links').html( '<ul>'+approveButton + rejectButton +'</ul>');
    // Add the approve/reject content buttons to the action bar


});


function rejectContentStatus() {
   $.post( ctx+'/changeContentStatus.ajax?contentStatus='+contentStatus+'&restaurantId='+restaurantId+'&mgn=' + (Math.random() * 99999999),
       function( data ) {
           if( data.success ) {
                buildOrder(data.order);
           } else {
                alert('success:' + data.success);
           }
       }
   );

}

function approveContentStatus() {

}