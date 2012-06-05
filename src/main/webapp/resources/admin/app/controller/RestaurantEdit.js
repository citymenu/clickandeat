// Global restaurant variables
var restaurant;
var address;
var mainContact;
var deliveryOptions;
var menu;

Ext.define('AD.controller.RestaurantEdit', {
    extend: 'Ext.app.Controller',
    stores:['Restaurants'],
    models: ['Restaurant','Person','Address','DeliveryOptions'],
    views:[
    	'restaurant.Edit',
    	'restaurant.MainDetails',
    	'restaurant.DeliveryDetails'
    ],

	init: function() {

        // Set up event handlers
        this.control({

            'restaurantmaindetails': {
                render:this.mainDetailsRendered
            },

            'restaurantdeliverydetails': {
                render:this.deliveryDetailsRendered
            },

            'restaurantedit button[action=close]': {
                click:this.close
            },
        });

        // Initialize models from restaurant JSON
	    restaurant = new AD.model.Restaurant(restaurantObj);
	    address = new AD.model.Address(restaurantObj.address);
	    mainContact = new AD.model.Person(restaurantObj.mainContact);
	    deliveryOptions = new AD.model.DeliveryOptions(restaurantObj.deliveryOptions);

    },

	close: function(button) {
	    location.href = ctx + '/admin/restaurants.html';
	},

    mainDetailsRendered: function(formPanel) {

        var checkboxgroup = formPanel.down('checkboxgroup');
        cuisines.forEach(function(cuisine){
            var checkbox = Ext.create('Ext.form.field.Checkbox',{
                name:'cuisines',
                boxLabel:cuisine,
                inputValue:cuisine,
                checked:restaurant.get('cuisines').indexOf(cuisine) != -1
            });
            checkboxgroup.items.add(checkbox);
        });

        // Populate form values
        formPanel.loadRecord(restaurant);
        formPanel.loadRecord(address);
        formPanel.loadRecord(mainContact);
    },

    deliveryDetailsRendered: function(formPanel) {


        // Get the opening times from the restaurant
        var openingTimes = restaurantObj.openingTimes;

        // Build opening times onto form
        var form = formPanel.getForm();
        form.findField('openingTimesSummary').setValue(openingTimes.openingTimesSummary);

        // Populate individual opening times
        openingTimes.openingTimes.forEach(function(openingTime){

        });

        // Populate form values from delivery options record
        formPanel.loadRecord(deliveryOptions);

    }

});