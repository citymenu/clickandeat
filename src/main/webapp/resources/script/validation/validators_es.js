// Validation regular expressions
var checkoutRegexps = ({
    firstName: /\S/,
    lastName: /\S/,
    telephone: /^[0-9]{2,3}-?\s?[0-9]{6,7}$/,
    email: /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    address1: /\S/,
    postCode: /^([0][1-9]|[1-4[0-9]){2}[0-9]{3}$/
});
