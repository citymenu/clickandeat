// Global restaurant variables
var restaurant;
var address;
var mainContact;
var deliveryOptions;
var menu;

Ext.define('AD.controller.RestaurantEdit', {
    extend: 'Ext.app.Controller',
    stores:['Restaurants','MenuCategories','MenuItems'],
    models: ['Restaurant','Person','Address','DeliveryOptions','Menu'],
    views:[
    	'restaurant.Edit',
    	'restaurant.MainDetails',
    	'restaurant.DeliveryDetails',
    	'restaurant.Menu'
    ],

    refs: [{
        ref:'menuCategoriesGrid',
        selector:'#menucategoriesgrid'
    }],

	init: function() {

        // Set up event handlers
        this.control({

            'restaurantmaindetails': {
                render:this.mainDetailsRendered
            },

            'restaurantdeliverydetails': {
                render:this.deliveryDetailsRendered
            },

            'restaurantmenu': {
                render:this.menuRendered
            },

            '#menucategoriesgrid': {
                select:this.menuCategoriesGridSelected
            },

            'restaurantedit button[action=close]': {
                click:this.close
            }

        });

        // Initialize models from restaurant JSON
	    restaurant = new AD.model.Restaurant(restaurantObj);
	    address = new AD.model.Address(restaurantObj.address);
	    mainContact = new AD.model.Person(restaurantObj.mainContact);
	    deliveryOptions = new AD.model.DeliveryOptions(restaurantObj.deliveryOptions);
	    menu = new AD.model.Menu(restaurantObj.menu);

    },

	close: function(button) {
	    location.href = ctx + '/admin/restaurants.html';
	},

    // Populate main details form
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

    // Populate delivery details form
    deliveryDetailsRendered: function(formPanel) {

        // Get the opening times from the restaurant
        var openingTimes = restaurantObj.openingTimes;

        // Build opening times onto form
        var form = formPanel.getForm();
        form.findField('openingTimesSummary').setValue(openingTimes.openingTimesSummary);
        form.findField('closedDates').setValue(openingTimes.closedDates.join('\n'));

        // Populate individual opening times
        openingTimes.openingTimes.forEach(function(openingTime){
            var day = openingTime.dayOfWeek;
            form.findField('open_' + day).setValue(openingTime.open);
            form.findField('collectionOpeningTime_' + day).setValue(openingTime.collectionOpeningTime);
            form.findField('collectionClosingTime_' + day).setValue(openingTime.collectionClosingTime);
            form.findField('deliveryOpeningTime_' + day).setValue(openingTime.deliveryOpeningTime);
            form.findField('deliveryClosingTime_' + day).setValue(openingTime.deliveryClosingTime);
        });

        // Populate form values from delivery options record
        formPanel.loadRecord(deliveryOptions);
    },

    // Initialize menu panel
    menuRendered: function(panel) {
        this.getMenuCategoriesStore().loadData(menu.get('menuCategories'));
        panel.down('#menuitemsgrid').view.on('drop',this.menuItemsGridDropped,this);
    },

    // Fires when a record is selected in the menu categories grid
    menuCategoriesGridSelected: function(rowmodel,record,index,options) {
        this.getMenuItemsStore().removeAll(false);
        this.getMenuItemsStore().loadData(record.get('menuItems'));
    },

    // Updates the menu categories grid after drag/drop
    menuItemsGridDropped: function(node, data, dropRec, dropPosition) {

        // Reorder menu items store
        this.getMenuItemsStore().remove(data.records[0]);
        var index = this.getMenuItemsStore().indexOf(dropRec);
        this.getMenuItemsStore().insert((dropPosition == 'after'? index+1: index), data.records[0]);

        // Update menu items on menu category
        var selectedMenuCategory = this.getMenuCategoriesGrid().getSelectionModel().getLastSelected();
        var index = this.getMenuCategoriesStore().indexOf(selectedMenuCategory);
        this.getMenuCategoriesStore().getAt(index).set('menuItems',this.getMenuItemsStore().getRange());

    }


});