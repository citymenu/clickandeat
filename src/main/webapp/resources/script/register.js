$(document).ready(function(){
    $("#username").datepicker({
        showAnim: 'fadeIn',
        dateFormat: 'dd/mm/yy',
        showOtherMonths: true,
        selectOtherMonths: true
    });
    $("input:submit").button();
});