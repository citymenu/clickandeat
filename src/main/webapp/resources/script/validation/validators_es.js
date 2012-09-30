// Validation regular expressions
var checkoutRegexps = ({
    firstName: /\S/,
    lastName: /\S/,
    telephone: /\S/,
    email: /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
    address1: /\S/,
    postCode: /\S/
});
