$(document).ready(function(){
    $('#signin').button();

    $('#proceed').button().click(function(){
        proceed();
    });

});

function proceed() {

    var person = new Object({
        firstName: $('#proceedForm input[name="firstName"]').val(),
        lastName: $('#proceedForm input[name="lastName"]').val(),
        email: $('#proceedForm input[name="email"]').val(),
        confirmEmail: $('#proceedForm input[name="confirmEmail"]').val(),
        telephone: $('#proceedForm input[name="telephone"]').val(),
        mobile: $('#proceedForm input[name="mobile"]').val()
    });

    $.post( ctx + '/secure/setcustomerdetails.ajax', { body: JSON.stringify(person) },
        function( data ) {
            if( data.success ) {
                alert('success');
            } else {
                alert('success:' + data.success);
            }
        }
    );

}
