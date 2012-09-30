/**
 * Simple field validator
 **/
function Validator(obj) {
    this.fieldName = obj.fieldName;
    this.regexp = obj.regexp;
    this.invalidText = obj.invalidText;

    this.validate = function() {
        var value = $('#' + this.fieldName).val();
        if( this.regexp.test(value)) {
            $('#' + this.fieldName + '-validation').removeClass('invalid');
            $('#' + this.fieldName + '-validation').addClass('valid');
            $('#' + this.fieldName + '-validation').prop('title',getLabel('validation.valid'));
            return true;
        } else {
            $('#' + this.fieldName + '-validation').removeClass('valid');
            $('#' + this.fieldName + '-validation').addClass('invalid');
            $('#' + this.fieldName + '-validation').prop('title',this.invalidText);
            return false;
        }
    }
}