
$(document).ready(function(){
    validateForm();

    // Add validation event handlers
    validators.each(function(fieldName,validator){
        $('#' + fieldName).change(function(){
            validateForm();
        });

        $('#' + fieldName).keyup(function(){
            validateForm();
        });
    });

});


// Updates validation on all form fields
function validateForm() {
    isValid = true;
    validators.each(function(fieldName,validator){
        if( $('#' + fieldName).is(":visible")) {
            if( !validator.validate()) {
                isValid = false;
            };
        }
    });
}

// Validation entries
var validators = new HashTable();

// Email address validation
validators.setItem('email',new Validator({
    fieldName: 'email',
    regexp: checkoutRegexps.email,
    invalidText: getLabel('validation.email.invalidText')
}));


// Send an email to the user (or us) with a voucher (or voucher request)
function sendVoucher(){
    alert("Not implemented yet");
}