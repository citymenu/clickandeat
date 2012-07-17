$(document).ready(function(){
    $('#deliveryoptionsbutton').button();
    $('#deliveryoptionsbutton').click(function(){
        deliveryOptions();
    });
    $('#placeorderbutton').button();
    $('#placeorderbutton').click(function(){
        placeOrder();
    });
});

// Override order config
function getOrderPanelConfig() {
    var config = {
        showDeliveryOptions: false,
        allowRemoveItems: false,
        enableCheckoutButton: false
    };
    return config;
}

// Back to delivery options
function deliveryOptions() {
    location.href = ctx + '/secure/checkout.html';
}

// Extract the credit card details from the form
function getCreditCard() {
    var creditCard = {
        cardType: $('input[name="cardType"]').val(),
        cardHoldersName: $('input[name="cardHoldersName"]').val(),
        cardNumber: $('input[name="cardNumber"]').val(),
        issueNumber: $('input[name="issueNumber"]').val(),
        expiryMonth: $('input[name="expiryMonth"]').val(),
        expiryYear: $('input[name="expiryYear"]').val(),
        securityCode: $('input[name="securityCode"]').val()
    }
    return creditCard;
}
