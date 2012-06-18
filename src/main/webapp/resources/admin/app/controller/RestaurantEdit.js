// Global restaurant variables
var restaurant;
var address;
var mainContact;
var notificationOptions;
var deliveryOptions;
var menu;

// Global menu category edit form variable
var menuCategoryEditForm;

// Global menu item edit form variable
var menuItemEditForm;

Ext.define('AD.controller.RestaurantEdit', {
    extend: 'Ext.app.Controller',
    stores:['Restaurants','MenuCategories','MenuItems'],
    models: [
        'Restaurant',
        'Person',
        'Address',
        'NotificationOptions',
        'DeliveryOptions',
        'Menu',
        'MenuItemTypeCost'
    ],
    views:[
    	'restaurant.Edit',
    	'restaurant.MainDetails',
    	'restaurant.DeliveryDetails',
    	'restaurant.Menu'
    ],

    refs: [{
        ref:'menuCategoriesGrid',
        selector:'#menucategoriesgrid'
    },{
        ref:'menuItemsGrid',
        selector:'#menuitemsgrid'
    },{
        ref:'menuEditForm',
        selector:'#menueditform'
    },{
        ref:'restaurantTabPanel',
        selector:'#restauranttabpanel'
    },{
        ref:'mainDetailsForm',
        selector:'restaurantmaindetails'
    },{
        ref:'deliveryDetailsForm',
        selector:'restaurantdeliverydetails'
    }],

	init: function() {

        // Set up event handlers
        this.control({

            'restaurantedit button[action=close]': {
                click:this.close
            },

            'restaurantedit button[action=saverestaurant]': {
                click:this.saveRestaurant
            },

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
                itemclick:this.menuCategoriesGridSelected
            },

            '#menucategoriesgrid button[action=create]': {
                click:this.createMenuCategory
            },

            'restaurantmenucategoryedit button[action=save]': {
                click:this.updateMenuCategory
            },

            'restaurantmenucategoryedit button[action=revert]': {
                click:this.revertMenuCategory
            },

            'restaurantmenucategoryedit button[action=remove]': {
                click:this.removeMenuCategory
            },

            'restaurantmenucategoryedit': {
                render:this.menuCategoryEditRendered
            },

            '#menuitemsgrid': {
                itemclick:this.menuItemsGridSelected
            },

            '#menuitemsgrid button[action=create]': {
                click:this.createMenuItem
            },

            'restaurantmenuitemedit button[action=save]': {
                click:this.updateMenuItem
            },

            'restaurantmenuitemedit button[action=revert]': {
                click:this.revertMenuItem
            },

            'restaurantmenuitemedit button[action=remove]': {
                click:this.removeMenuItem
            },

            'restaurantmenuitemedit': {
                render:this.menuItemEditRendered
            }

        });

        // Initialize models from restaurant JSON
	    restaurant = new AD.model.Restaurant(restaurantObj);
	    address = new AD.model.Address(restaurantObj.address);
	    mainContact = new AD.model.Person(restaurantObj.mainContact);
	    notificationOptions = new AD.model.NotificationOptions(restaurantObj.notificationOptions);
	    deliveryOptions = new AD.model.DeliveryOptions(restaurantObj.deliveryOptions);

	    // Initialize the menu object using fully qualified model records
	    var menuCategories = [];
	    restaurantObj.menu.menuCategories.forEach(function(category) {
	        var menuCategory = new AD.model.MenuCategory(category);
	        var menuItems = [];
	        category.menuItems.forEach(function(item) {
	            var menuItem = new AD.model.MenuItem(item);
	            var menuItemTypeCosts = [];
	            item.menuItemTypeCosts.forEach(function(itemType){
	                var menuItemTypeCost = new AD.model.MenuItemTypeCost(itemType);
	                menuItemTypeCosts.push(menuItemTypeCost);
	            });
	            menuItem.set('menuItemTypeCosts',menuItemTypeCosts);
	            menuItems.push(menuItem);
	        });
	        menuCategory.set('menuItems',menuItems);
	        menuCategories.push(menuCategory);
	    });

	    menu = new AD.model.Menu(restaurantObj.menu);
	    menu.set('menuCategories',menuCategories);

    },

	close: function(button) {
	    location.href = ctx + '/admin/restaurants.html';
	},

    saveRestaurant: function(button) {

        // Check if the main details form is invalid`
        if(!this.getMainDetailsForm().getForm().isValid()) {
            this.getRestaurantTabPanel().setActiveTab(0);
            this.showInvalidFormWarning();
            return;
        }

        // Check if the delivery details form is invalid`
        if(!this.getDeliveryDetailsForm().getForm().isValid()) {
            this.getRestaurantTabPanel().setActiveTab(1);
            this.showInvalidFormWarning();
            return;
        }

        // Get the input values from the main details form
        var mainDetailValues = this.getMainDetailsForm().getForm().getValues();

        // Update the restaurant object details from the main details form
        restaurantObj.name = mainDetailValues['name'];
        restaurantObj.description = mainDetailValues['description'].replace('&#8203;','');
        restaurantObj.contactEmail = mainDetailValues['contactEmail'];
        restaurantObj.contactTelephone = mainDetailValues['contactTelephone'];
        restaurantObj.contactMobile = mainDetailValues['contactMobile'];
        restaurantObj.website = mainDetailValues['website'];

        // Build cuisines array onto restaurant object
        var cuisines = [];
        this.getMainDetailsForm().down('checkboxgroup').items.each(function(checkBoxItem) {
            if(checkBoxItem.getValue()) {
                cuisines.push(checkBoxItem.inputValue);
            }
        });
        restaurantObj.cuisines = cuisines;

        // Update address details for the restaurant
        restaurantObj.address = new Object();
        restaurantObj.address.address1 = mainDetailValues['address1'];
        restaurantObj.address.address2 = mainDetailValues['address2'];
        restaurantObj.address.address3 = mainDetailValues['address3'];
        restaurantObj.address.town = mainDetailValues['town'];
        restaurantObj.address.region = mainDetailValues['region'];
        restaurantObj.address.postCode = mainDetailValues['postCode'];

        // Update main contact details for the restaurant
        restaurantObj.mainContact = new Object();
        restaurantObj.mainContact.firstName = mainDetailValues['firstName'];
        restaurantObj.mainContact.lastName = mainDetailValues['lastName'];
        restaurantObj.mainContact.telephone = mainDetailValues['telephone'];
        restaurantObj.mainContact.mobile = mainDetailValues['mobile'];
        restaurantObj.mainContact.email = mainDetailValues['email'];

        // Update notification options for the restuarant
        restaurantObj.notificationOptions = new Object();
        restaurantObj.notificationOptions.receiveNotificationCall = mainDetailValues['receiveNotificationCall'] == 'on';
        restaurantObj.notificationOptions.takeOrderOverTelephone = mainDetailValues['takeOrderOverTelephone'] == 'on';
        restaurantObj.notificationOptions.receiveSMSNotification = mainDetailValues['receiveSMSNotification'] == 'on';
        restaurantObj.notificationOptions.notificationPhoneNumber = mainDetailValues['notificationPhoneNumber'];
        restaurantObj.notificationOptions.notificationSMSNumber = mainDetailValues['notificationSMSNumber'];
        restaurantObj.notificationOptions.notificationEmailAddress = mainDetailValues['notificationEmailAddress'];

        // Get the input values from the delivery details form
        var deliveryDetailValues = this.getDeliveryDetailsForm().getForm().getValues();

        // Update opening times for the restaurant
        restaurantObj.openingTimes = new Object();
        restaurantObj.openingTimes.openingTimesSummary = deliveryDetailValues['openingTimesSummary'].replace('&#8203;','');

        // Build daily opening times summary
        var openingTimes = [];
        for( i = 1; i < 8; i++ ) {
            var openingTime = new Object();
            openingTime.dayOfWeek = i;
            openingTime.open = deliveryDetailValues['open_' + i] == 'on';
            openingTime.collectionOpeningTime = deliveryDetailValues['collectionOpeningTime_' + i];
            openingTime.collectionClosingTime = deliveryDetailValues['collectionClosingTime_' + i];
            openingTime.deliveryOpeningTime = deliveryDetailValues['deliveryOpeningTime_' + i];
            openingTime.deliveryClosingTime = deliveryDetailValues['deliveryClosingTime_' + i];
            openingTimes.push(openingTime);
        }
        restaurantObj.openingTimes.openingTimes = openingTimes;
        restaurantObj.openingTimes.closedDates = delimitedStringToArray(deliveryDetailValues['closedDates'],'\n');

        // Build delivery options details
        restaurantObj.deliveryOptions = new Object();
        restaurantObj.deliveryOptions.deliveryOptionsSummary = deliveryDetailValues['deliveryOptionsSummary'].replace('&#8203;','');
        restaurantObj.deliveryOptions.deliveryTimeMinutes = deliveryDetailValues['deliveryTimeMinutes'];
        restaurantObj.deliveryOptions.minimumOrderForFreeDelivery = deliveryDetailValues['minimumOrderForFreeDelivery'];
        restaurantObj.deliveryOptions.deliveryCharge = deliveryDetailValues['deliveryCharge'];
        restaurantObj.deliveryOptions.collectionDiscount = deliveryDetailValues['collectionDiscount'];
        restaurantObj.deliveryOptions.minimumOrderForCollectionDiscount = deliveryDetailValues['minimumOrderForCollectionDiscount'];
        restaurantObj.deliveryOptions.deliveryRadiusInKilometres = deliveryDetailValues['deliveryRadiusInKilometres'];
        restaurantObj.deliveryOptions.areasDeliveredTo = delimitedStringToArray(deliveryDetailValues['areasDeliveredTo'],'\n');

        // Build restaurant menu
        restaurantObj.menu = new Object();
        var menuCategories = [];
        this.getMenuCategoriesStore().getRange().forEach(function(category){
            var menuCategory = new Object();
            menuCategory.name = category.get('name');
            menuCategory.categoryId = category.get('categoryId');
            menuCategory.type = category.get('type');
            menuCategory.summary = category.get('summary');
            menuCategory.iconClass = category.get('iconClass');
            menuCategory.itemTypes = delimitedStringToArray(category.get('itemTypes'),'\n');
            var menuItems = [];
            category.get('menuItems').forEach(function(item){
                var menuItem = new Object();
                menuItem.number = item.get('number');
                menuItem.itemId = item.get('itemId');
                menuItem.title = item.get('title');
                menuItem.subtitle = item.get('subtitle');
                menuItem.description = item.get('description');
                menuItem.iconClass = item.get('iconClass');
                menuItem.cost = item.get('cost');
                var menuItemTypeCosts = [];
                item.get('menuItemTypeCosts').forEach(function(itemTypeCost){
                    var menuItemTypeCost = new Object();
                    menuItemTypeCost.type = itemTypeCost.get('type');
                    menuItemTypeCost.cost = itemTypeCost.get('cost');
                    menuItemTypeCosts.push(menuItemTypeCost);
                });
                menuItem.menuItemTypeCosts = menuItemTypeCosts;
                menuItems.push(menuItem);
            });
            menuCategory.menuItems = menuItems;
            menuCategories.push(menuCategory);
        });
        restaurantObj.menu.menuCategories = menuCategories;

        // Submit the restaurant to the server
        Ext.Ajax.request({
            url: ctx + '/admin/restaurants/save.ajax',
            method:'POST',
            params: {
                body: JSON.stringify(restaurantObj)
            },
            success: function(response) {
                var obj = Ext.decode(response.responseText);
                if( obj.success ) {
                    restaurantObj.id = obj.id;
                    showSuccessMessage(Ext.get('restauranteditpanel'),'Saved','Restaurant details updated successfully');
                } else {
                    showErrorMessage(Ext.get('restauranteditpanel'),'Error',obj.message);
                }
            },
            failure: function(response) {
                var obj = Ext.decode(response.responseText);
                showErrorMessage(Ext.get('restauranteditpanel'),'Error',obj.message);
            },
            scope:this
        });

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
        formPanel.loadRecord(notificationOptions);
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

    // Fires when the add button is clicked on the menu category grid
    createMenuCategory: function(button) {
        var menuCategory = new AD.model.MenuCategory({
            name:'New Category',
            type:'STANDARD',
            menuItems:[],
            itemTypes:[]
        });
        this.getMenuCategoriesStore().add(menuCategory);
    },

    // Fires when the remove button is clicked on the menu category grid
    removeMenuCategory: function(button) {
        Ext.MessageBox.show({
            title:'Delete Menu Category',
            msg:'Are you sure you want to remove this menu category?',
            buttons:Ext.MessageBox.YESNO,
            icon:Ext.MessageBox.QUESTION,
            closable:false,
            fn:function(result) {
                if(result == 'yes') {
                    this.getMenuCategoriesStore().remove(menuCategoryEditForm.getRecord());
                    this.getMenuItemsStore().removeAll(false);
                    this.getMenuEditForm().removeAll(true);
                    menuCategoryEditForm = null;
                    showSuccessMessage(Ext.get('restauranteditpanel'),'Deleted','Menu category has been deleted');
                }
            },
            scope:this
        });
    },

    // Fires when the save button is clicked on the menu category edit form
    updateMenuCategory: function(button) {
        if(!menuCategoryEditForm.getForm().isValid()) {
            this.showInvalidFormWarning();
        } else {
            var index = this.getMenuCategoriesStore().indexOf(menuCategoryEditForm.getRecord());
            this.getMenuCategoriesStore().getAt(index).set(menuCategoryEditForm.getValues());
            showSuccessMessage(Ext.get('restauranteditpanel'),'Saved','Menu category details have been updated');
        }
    },

    // Fires when the revert button is clicked on the menu category edit form
    revertMenuCategory: function(button) {
        menuCategoryEditForm.getForm().reset();
        menuCategoryEditForm.loadRecord(menuCategoryEditForm.getRecord());
        showSuccessMessage(Ext.get('restauranteditpanel'),'Reverted','Menu category details have been reverted');
    },

    // Fires when a record is selected in the menu categories grid
    menuCategoriesGridSelected: function(rowmodel,record,item,index,evt,options) {

        // If the item is already selected, do nothing
        var selectedMenuCategory = this.getMenuCategoriesGrid().getSelectionModel().getLastSelected();
        if( menuCategoryEditForm && menuCategoryEditForm.getRecord() == selectedMenuCategory ) {
            return; // Do nothing on purpose
        }

        this.getMenuItemsStore().removeAll(false);
        this.getMenuItemsStore().loadData(record.get('menuItems'));

        var form = Ext.create('AD.view.restaurant.MenuCategoryEdit');
        menuCategoryEditForm = form; // Update global variable
        menuItemEditForm = null; // Update global variable
        this.getMenuEditForm().removeAll(true);
        this.getMenuEditForm().add(form);
    },

    // Updates the menu categories grid after drag/drop
    menuItemsGridDropped: function(node, data, dropRec, dropPosition) {

        // Reorder menu items store
        this.getMenuItemsStore().remove(data.records[0]);
        var index = this.getMenuItemsStore().indexOf(dropRec);
        this.getMenuItemsStore().insert((dropPosition == 'after'? index+1: index), data.records[0]);

        // Update menu items on menu category
        this.updateMenuCategoryItems();
    },

    // Loads the selected menu category into the edit form
    menuCategoryEditRendered: function(formPanel) {
        var selectedMenuCategory = this.getMenuCategoriesGrid().getSelectionModel().getLastSelected();
        menuCategoryEditForm.loadRecord(selectedMenuCategory);
    },

    // Fires when a record is selected in the menu items grid
    menuItemsGridSelected: function(rowmodel,record,item,index,evt,options) {

        // If the item is already selected, do nothing
        var selectedMenuItem = this.getMenuItemsGrid().getSelectionModel().getLastSelected();
        if( menuItemEditForm && menuItemEditForm.getRecord() == selectedMenuItem ) {
            return; // Do nothing on purpose
        }

        var form = Ext.create('AD.view.restaurant.MenuItemEdit');
        menuItemEditForm = form; // Update global variable
        menuCategoryEditForm = null; // Update global variable
        this.getMenuEditForm().removeAll(true);
        this.getMenuEditForm().add(form);
    },

    // Fires when the add button is clicked on the menu item grid
    createMenuItem: function(button) {

        // Get the selected menu category
        var selectedMenuCategory = this.getMenuCategoriesGrid().getSelectionModel().getLastSelected();
        if( !selectedMenuCategory ) {
            return; // No category selected, no nothing
        }

        // Create new menu item and add to the store
        var menuItem = new AD.model.MenuItem({
            title:'New Item',
            menuItemTypeCosts:[]
        });
        this.getMenuItemsStore().add(menuItem);

        // Push new menu item into menu categories store
        var index = this.getMenuCategoriesStore().indexOf(selectedMenuCategory);
        this.getMenuCategoriesStore().getAt(index).set('menuItems',this.getMenuItemsStore().getRange());
    },

    // Fires when the remove button is clicked on the menu item grid
    removeMenuItem: function(button) {
        Ext.MessageBox.show({
            title:'Delete Menu Item',
            msg:'Are you sure you want to remove this menu item?',
            buttons:Ext.MessageBox.YESNO,
            icon:Ext.MessageBox.QUESTION,
            closable:false,
            fn:function(result) {
                if(result == 'yes') {
                    this.getMenuItemsStore().remove(menuItemEditForm.getRecord());
                    this.getMenuEditForm().removeAll(true);
                    menuItemEditForm = null;
                    // Update menu items on menu category
                    this.updateMenuCategoryItems();
                    showSuccessMessage(Ext.get('restauranteditpanel'),'Deleted','Menu item has been deleted');
                }
            },
            scope:this
        });
    },

    // Fires when the save button is clicked on the menu item edit form
    updateMenuItem: function(button) {

        if(!menuItemEditForm.getForm().isValid()) {
            this.showInvalidFormWarning();
        } else {
            var index = this.getMenuItemsStore().indexOf(menuItemEditForm.getRecord());
            var record = this.getMenuItemsStore().getAt(index);
            record.set(menuItemEditForm.getValues());

            // Populate menu item type costs if present
            var menuItemTypeCosts = [];
            menuItemEditForm.getForm().getFields().each(function(field){
                var fieldName = field.getName();
                if(fieldName.indexOf('cost_') != -1 ) {
                    var fieldValue = field.getValue();
                    if( fieldValue && fieldValue != 0 ) {
                        var menuItemTypeCost = new AD.model.MenuItemTypeCost({
                            type:fieldName.substring(5).replace('_',' '),
                            cost:fieldValue
                        });
                        menuItemTypeCosts.push(menuItemTypeCost);
                    }
                }
            });
            record.set('menuItemTypeCosts',menuItemTypeCosts);

            // Update menu items on menu category
            this.updateMenuCategoryItems();

            // Show success message
            showSuccessMessage(Ext.get('restauranteditpanel'),'Saved','Menu item details have been updated');
        }
    },

    // Fires when the revert button is clicked on the menu item edit form
    revertMenuItem: function(button) {
        menuItemEditForm.getForm().reset();
        menuItemEditForm.loadRecord(menuItemEditForm.getRecord());
        menuItemEditForm.getRecord().get('menuItemTypeCosts').forEach(function(menuItemTypeCost){
            var field = menuItemEditForm.getForm().findField('cost_' + menuItemTypeCost.get('type').replace(' ','_'));
            if( field ) {
                field.setValue(menuItemTypeCost.get('cost'));
            }
        });
        showSuccessMessage(Ext.get('restauranteditpanel'),'Reverted','Menu item details have been reverted');
    },

    // Loads the selected menu item into the edit form
    menuItemEditRendered: function(formPanel) {
        var selectedMenuItem = this.getMenuItemsGrid().getSelectionModel().getLastSelected();
        var selectedMenuCategory = this.getMenuCategoriesGrid().getSelectionModel().getLastSelected();
        if( selectedMenuCategory.get('type') == 'GRID') {
            var itemTypes = selectedMenuCategory.get('itemTypes');
            if( itemTypes ) {

                // Create field container for item type costs
                var itemTypesContainer = Ext.create('Ext.form.FieldContainer', {
                    fieldLabel:'Item type costs',
                    labelAlign:'top',
                    defaults:{
                        layout:'anchor',
                        anchor:'100%'
                    }
                });

                // Generate an entry field for the item type cost
                itemTypes.split('\n').forEach(function(itemType){
                    var field = Ext.create('Ext.form.field.Number', {
                        fieldLabel:itemType,
                        name:'cost_' + itemType.replace(' ','_')
                    });
                    itemTypesContainer.add(field);
                });

                // Add the field container to the form
                menuItemEditForm.add(itemTypesContainer);
            }
        }

        // Load the record onto the form
        menuItemEditForm.loadRecord(selectedMenuItem);

        // Populate menuItemTypeCosts if set
        selectedMenuItem.get('menuItemTypeCosts').forEach(function(menuItemTypeCost){
            var field = menuItemEditForm.getForm().findField('cost_' + menuItemTypeCost.get('type').replace(' ','_'));
            if( field ) {
                field.setValue(menuItemTypeCost.get('cost'));
            }
        });

    },

    // Updates menu items store into selected menu category menuItems attribute
    updateMenuCategoryItems:function() {
       var selectedMenuCategory = this.getMenuCategoriesGrid().getSelectionModel().getLastSelected();
       var categoryIndex = this.getMenuCategoriesStore().indexOf(selectedMenuCategory);
       this.getMenuCategoriesStore().getAt(categoryIndex).set('menuItems',this.getMenuItemsStore().getRange());
    },

    // Shows a warning alert box
    showInvalidFormWarning: function() {
        Ext.MessageBox.show({
            title:'Input Values Not Valid',
            msg:'Please check all required fields have been entered correctly',
            buttons:Ext.MessageBox.OK,
            icon:Ext.MessageBox.WARNING,
            closable:false
        });
    }

});