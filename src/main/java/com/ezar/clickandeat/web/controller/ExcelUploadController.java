package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.SequenceGenerator;
import com.ezar.clickandeat.validator.*;
import com.ezar.clickandeat.validator.excel.ExcelObjectValidator;
import com.ezar.clickandeat.validator.excel.ExcelObjectValidatorImpl;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.*;

import static com.ezar.clickandeat.web.controller.helper.ExcelUtils.*;

@Controller
public class ExcelUploadController {

    private static final Logger LOGGER = Logger.getLogger(ExcelUploadController.class);

    private static final int MAX_ALLOWED_EMPTY_ROWS = 100;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private CuisineProvider cuisineProvider;

    @Autowired
    private JSONUtils jsonUtils;

    @Autowired
    private ObjectValidator<Restaurant> restaurantValidator;

    private ExcelObjectValidator<MenuCategory> menuCategoryValidator = new ExcelObjectValidatorImpl<MenuCategory>(new MenuCategoryValidator());
    private ExcelObjectValidator<MenuItem> menuItemValidator = new ExcelObjectValidatorImpl<MenuItem>(new MenuItemValidator());
    
    
    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

    
    @ResponseBody
    @RequestMapping(value="/admin/menu/upload.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> uploadRestaurant(@RequestParam("file") CommonsMultipartFile file) throws Exception {

        Map<String,Object> model = new HashMap<String,Object>();
        
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

            // Build restaurant from workbook
            ValidationErrors excelErrors = new ValidationErrors();
            Restaurant restaurant = buildRestaurantFromWorkbook(workbook,excelErrors);
            
            // Now validate the restaurant
            ValidationErrors errors = restaurantValidator.validate(restaurant);
            errors.getErrors().addAll(excelErrors.getErrors());

            // If there are no errors, then we save the restaurant object
            if(!errors.hasErrors()) {
                if(StringUtils.hasText(restaurant.getRestaurantId())) {
                    copyExistingRestaurantDetails(restaurant);
                    restaurantRepository.saveRestaurant(restaurant);
                }
                else {
                    // Just save the new restaurant
                    restaurant.setRestaurantId(sequenceGenerator.getNextSequence());
                    restaurantRepository.saveRestaurant(restaurant);
                }
            }
            
            // Any errors, return them now
            if( errors.hasErrors()) {
                model.put("success",false);
                model.put("errors",errors.getErrors());
            }
            else {
                // All OK
                model.put("success",true);
            }
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        String json = jsonUtils.serializeAndEscape(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    /**
     * Get existing restaurant and copy over identifier and status values
     * @param restaurant
     */

    private void copyExistingRestaurantDetails(Restaurant restaurant) {
        Restaurant existingRestaurant = restaurantRepository.findByRestaurantId(restaurant.getRestaurantId());
        restaurant.setId(existingRestaurant.getId());
        restaurant.setContentApproved(existingRestaurant.isContentApproved());
        restaurant.setCreated(existingRestaurant.getCreated());
        restaurant.setHasUploadedImage(existingRestaurant.isHasUploadedImage());
        restaurant.setContentStatus(existingRestaurant.getContentStatus());
        restaurant.setLastContentApprovalStatusUpdated(existingRestaurant.getLastContentApprovalStatusUpdated());
        restaurant.setLastOrderReponseTime(existingRestaurant.getLastOrderReponseTime());
        restaurant.setRejectionReasons(existingRestaurant.getRejectionReasons());
        restaurant.setRestaurantUpdates(existingRestaurant.getRestaurantUpdates());
    }


    /**
     * @param workbook
     * @return
     */
    
    private Restaurant buildRestaurantFromWorkbook(XSSFWorkbook workbook,ValidationErrors errors) {
        Restaurant restaurant = new Restaurant();

        // Core data
        restaurant.setRestaurantId(getNamedCellStringValue(workbook, "Restaurant.Id"));
        restaurant.setName(getNamedCellStringValue(workbook, "Restaurant.Name"));
        restaurant.setDescription(getNamedCellStringValue(workbook, "Restaurant.Description"));

        // Address
        restaurant.getAddress().setAddress1(getNamedCellStringValue(workbook, "Restaurant.Address.Address1"));
        restaurant.getAddress().setTown(getNamedCellStringValue(workbook, "Restaurant.Address.Town"));
        restaurant.getAddress().setRegion(getNamedCellStringValue(workbook, "Restaurant.Address.Region"));
        restaurant.getAddress().setPostCode(getNamedCellStringValue(workbook, "Restaurant.Address.Postcode"));
        
        // Contact details
        restaurant.setContactTelephone(getNamedCellStringValue(workbook, "Restaurant.Contact.Telephone"));
        restaurant.setContactMobile(getNamedCellStringValue(workbook, "Restaurant.Contact.Mobile"));
        restaurant.setContactEmail(getNamedCellStringValue(workbook, "Restaurant.Contact.Email"));
        restaurant.setWebsite(getNamedCellStringValue(workbook, "Restaurant.Contact.Website"));

        // Main contact
        restaurant.getMainContact().setFirstName(getNamedCellStringValue(workbook, "Restaurant.MainContact.FirstName"));
        restaurant.getMainContact().setLastName(getNamedCellStringValue(workbook, "Restaurant.MainContact.LastName"));
        restaurant.getMainContact().setTelephone(getNamedCellStringValue(workbook, "Restaurant.MainContact.Telephone"));
        restaurant.getMainContact().setEmail(getNamedCellStringValue(workbook, "Restaurant.MainContact.Email"));

        // Notification details
        restaurant.getNotificationOptions().setReceiveNotificationCall("Y".equals(getNamedCellStringValue(workbook, "Restaurant.Notification.ReceiveCall")));
        restaurant.getNotificationOptions().setReceiveSMSNotification("Y".equals(getNamedCellStringValue(workbook, "Restaurant.Notification.ReceiveSMS")));
        restaurant.getNotificationOptions().setNotificationPhoneNumber(getNamedCellStringValue(workbook, "Restaurant.Notification.Telephone"));
        restaurant.getNotificationOptions().setNotificationSMSNumber(getNamedCellStringValue(workbook, "Restaurant.Notification.SMS"));
        restaurant.getNotificationOptions().setNotificationEmailAddress(getNamedCellStringValue(workbook, "Restaurant.Notification.Email"));
        restaurant.getNotificationOptions().setPrinterEmailAddress(getNamedCellStringValue(workbook, "Restaurant.Notification.PrinterEmail"));

        // Administration
        restaurant.setListOnSite("Y".equals(getNamedCellStringValue(workbook, "Restaurant.IncludeOnSite")));
        restaurant.setPhoneOrdersOnly("Y".equals(getNamedCellStringValue(workbook, "Restaurant.PhoneOrdersOnly")));
        restaurant.setRecommended("Y".equals(getNamedCellStringValue(workbook, "Restaurant.Recommended")));
        restaurant.setSearchRanking((int) getNamedCellDoubleValue(workbook, "Restaurant.SearchRanking"));
        restaurant.setCommissionPercent(getNamedCellDoubleValue(workbook, "Restaurant.Commission"));

        // Opening times
        XSSFCell openingTimesAnchor = getFirstCell(workbook, "Restaurant.OpeningTimes");
        int openingTimesAnchorRow = openingTimesAnchor.getRowIndex();
        int openingTimesAnchorCol = openingTimesAnchor.getColumnIndex();
        XSSFSheet openingTimesSheet = openingTimesAnchor.getSheet();
        OpeningTimes openingTimes = restaurant.getOpeningTimes();
        List<OpeningTime> openingTimeList = new ArrayList<OpeningTime>();
        for(int i = 0; i < 7; i++ ) {
            OpeningTime openingTime = new OpeningTime();
            openingTime.setDayOfWeek(i + 1);
            XSSFRow row = getRow(openingTimesSheet, openingTimesAnchorRow + i);
            openingTime.setOpen("Y".equals(getCell(row, openingTimesAnchorCol).getStringCellValue()));
            openingTime.setEarlyOpeningTime(getLocalTime(row.getCell(openingTimesAnchorCol + 1)));
            openingTime.setEarlyClosingTime(getLocalTime(row.getCell(openingTimesAnchorCol + 2)));
            openingTime.setLateOpeningTime(getLocalTime(row.getCell(openingTimesAnchorCol + 3)));
            openingTime.setLateClosingTime(getLocalTime(row.getCell(openingTimesAnchorCol + 4)));
            openingTimeList.add(openingTime);
        }
        openingTimes.setOpeningTimes(openingTimeList);
        XSSFRow bankHolidayRow = getRow(openingTimesSheet, openingTimesAnchorRow + 7);
        openingTimes.getBankHolidayOpeningTimes().setOpen("Y".equals(getCell(bankHolidayRow, openingTimesAnchorCol).getStringCellValue()));
        openingTimes.getBankHolidayOpeningTimes().setEarlyOpeningTime(getLocalTime(bankHolidayRow.getCell(openingTimesAnchorCol + 1)));
        openingTimes.getBankHolidayOpeningTimes().setEarlyClosingTime(getLocalTime(bankHolidayRow.getCell(openingTimesAnchorCol + 2)));
        openingTimes.getBankHolidayOpeningTimes().setLateOpeningTime(getLocalTime(bankHolidayRow.getCell(openingTimesAnchorCol + 3)));
        openingTimes.getBankHolidayOpeningTimes().setLateClosingTime(getLocalTime(bankHolidayRow.getCell(openingTimesAnchorCol + 4)));

        // Closed dates
        XSSFCell closingTimesCell = getNamedCell(workbook, "ClosedDates");
        int closedDateIndex = 1;
        XSSFRow closedDateRow = getRow(openingTimesSheet, closingTimesCell.getRowIndex() + closedDateIndex );
        LocalDate closedDate = getLocalDate(closedDateRow.getCell(1));
        while(closedDate != null ) {
            openingTimes.getClosedDates().add(closedDate);
            closedDateIndex++;
            closedDateRow = getRow(openingTimesSheet, closingTimesCell.getRowIndex() + closedDateIndex );
            closedDate = getLocalDate(closedDateRow.getCell(1));
        }

        // Delivery details
        XSSFSheet deliveryDetailsSheet = workbook.getSheet("Delivery Details");
        restaurant.getDeliveryOptions().setCollectionOnly("Y".equals(getNamedCellStringValue(workbook, "Restaurant.Delivery.CollectionOnly")));
        restaurant.getDeliveryOptions().setDeliveryTimeMinutes((int) getNamedCellDoubleValue(workbook, "Restaurant.Delivery.DeliveryTime"));
        restaurant.getDeliveryOptions().setCollectionTimeMinutes((int) getNamedCellDoubleValue(workbook, "Restaurant.Delivery.CollectionTime"));
        restaurant.getDeliveryOptions().setDeliveryCharge(getNamedCellDoubleValue(workbook, "Restaurant.Delivery.Charge"));
        restaurant.getDeliveryOptions().setAllowFreeDelivery("Y".equals(getNamedCellStringValue(workbook, "Restaurant.Delivery.AllowFreeDelivery")));
        restaurant.getDeliveryOptions().setMinimumOrderForDelivery(getNamedCellDoubleValue(workbook, "Restaurant.Delivery.MinimumOrderForDelivery"));
        restaurant.getDeliveryOptions().setMinimumOrderForFreeDelivery(getNamedCellDoubleValue(workbook, "Restaurant.Delivery.MinimumOrderForFreeDelivery"));
        restaurant.getDeliveryOptions().setAllowDeliveryBelowMinimumForFreeDelivery("Y".equals(getNamedCellStringValue(workbook, "Restaurant.Delivery.AllowOrdersBelowMinimumForFreeDelivery")));
        restaurant.getDeliveryOptions().setDeliveryRadiusInKilometres(getNamedCellDoubleValue(workbook, "Restaurant.Delivery.DeliveryRadius"));
        String areasDeliveredTo = getNamedCellStringValue(workbook, "Restaurant.Delivery.AreasDeliveredTo");
        if( StringUtils.hasText(areasDeliveredTo)) {
            restaurant.getDeliveryOptions().setAreasDeliveredTo(new ArrayList<String>(StringUtils.commaDelimitedListToSet(areasDeliveredTo)));
        }

        // Areas with same delivery charge
        XSSFCell sameDeliveryChargeAnchor = getFirstCell(workbook, "Restaurant.Delivery.SameDeliveryCharge");
        int sameDeliveryChargeRow = sameDeliveryChargeAnchor.getRowIndex();
        int sameDeliveryChargeCol = sameDeliveryChargeAnchor.getColumnIndex();
        int sameDeliveryChargeIndex = 1;
        String sameDeliveryChargeAreas = getCellStringValue(deliveryDetailsSheet, sameDeliveryChargeRow, sameDeliveryChargeCol);
        double sameDeliveryChargeAmount = getCellDoubleValue(deliveryDetailsSheet,  sameDeliveryChargeRow, sameDeliveryChargeCol + 1);
        while(StringUtils.hasText(sameDeliveryChargeAreas)) {
            AreaDeliveryCharge charge = new AreaDeliveryCharge();
            charge.setAreas(new ArrayList<String>(StringUtils.commaDelimitedListToSet(sameDeliveryChargeAreas)));
            charge.setDeliveryCharge(sameDeliveryChargeAmount);
            restaurant.getDeliveryOptions().getAreaDeliveryCharges().add(charge);
            sameDeliveryChargeAreas = getCellStringValue(deliveryDetailsSheet, sameDeliveryChargeRow + sameDeliveryChargeIndex, sameDeliveryChargeCol);
            sameDeliveryChargeAmount = getCellDoubleValue(deliveryDetailsSheet,  sameDeliveryChargeRow + sameDeliveryChargeIndex, sameDeliveryChargeCol + 1);
            sameDeliveryChargeIndex++;
        }

        // Areas with same minimum order value for delivery
        XSSFCell sameMinimumOrderValueAnchor = getFirstCell(workbook, "Restaurant.Delivery.SameMinimumOrder");
        int sameMinimumOrderValueRow = sameMinimumOrderValueAnchor.getRowIndex();
        int sameMinimumOrderValueCol = sameMinimumOrderValueAnchor.getColumnIndex();
        int sameMinimumOrderValueIndex = 1;
        String sameMinimumOrderValueAreas = getCellStringValue(deliveryDetailsSheet, sameMinimumOrderValueRow, sameMinimumOrderValueCol);
        double sameMinimumOrderValueAmount = getCellDoubleValue(deliveryDetailsSheet,  sameMinimumOrderValueRow, sameMinimumOrderValueCol + 1);
        while(StringUtils.hasText(sameMinimumOrderValueAreas)) {
            AreaDeliveryCharge charge = new AreaDeliveryCharge();
            charge.setAreas(new ArrayList<String>(StringUtils.commaDelimitedListToSet(sameMinimumOrderValueAreas)));
            charge.setDeliveryCharge(sameMinimumOrderValueAmount);
            restaurant.getDeliveryOptions().getAreaMinimumOrderCharges().add(charge);
            sameMinimumOrderValueAreas = getCellStringValue(deliveryDetailsSheet, sameMinimumOrderValueRow + sameMinimumOrderValueIndex, sameMinimumOrderValueCol);
            sameMinimumOrderValueAmount = getCellDoubleValue(deliveryDetailsSheet,  sameMinimumOrderValueRow + sameMinimumOrderValueIndex, sameMinimumOrderValueCol + 1);
            sameMinimumOrderValueIndex++;
        }

        // Same delivery charges by minimum order amount
        XSSFCell sameMinimumOrderAmountAnchor = getFirstCell(workbook, "Restaurant.Delivery.SameOrderAmount");
        int sameMinimumOrderAmountRow = sameMinimumOrderAmountAnchor.getRowIndex();
        int sameMinimumOrderAmountCol = sameMinimumOrderAmountAnchor.getColumnIndex();
        int sameMinimumOrderAmountIndex = 1;
        double minimumOrderValue = getCellDoubleValue(deliveryDetailsSheet, sameMinimumOrderAmountRow, sameMinimumOrderAmountCol);
        double deliveryCharge = getCellDoubleValue(deliveryDetailsSheet,  sameMinimumOrderValueRow, sameMinimumOrderValueCol + 1);
        while(minimumOrderValue != 0d) {
            VariableDeliveryCharge charge = new VariableDeliveryCharge();
            charge.setMinimumOrderValue(minimumOrderValue);
            charge.setDeliveryCharge(deliveryCharge);
            restaurant.getDeliveryOptions().getVariableDeliveryCharges().add(charge);
            minimumOrderValue = getCellDoubleValue(deliveryDetailsSheet, sameMinimumOrderAmountRow + sameMinimumOrderAmountIndex, sameMinimumOrderAmountCol);
            deliveryCharge = getCellDoubleValue(deliveryDetailsSheet,  sameMinimumOrderAmountRow + sameMinimumOrderAmountIndex, sameMinimumOrderAmountCol + 1);
            sameMinimumOrderAmountIndex++;
        }

        // Cuisines
        XSSFSheet cuisinesSheet = workbook.getSheet("Cuisines");
        int cuisineIndex = 1;
        String cuisine = getCellStringValue(cuisinesSheet, cuisineIndex, 0);
        while( StringUtils.hasText(cuisine)) {
            boolean cuisineSelected = "Y".equals(getCellStringValue(cuisinesSheet, cuisineIndex, 1));
            if( cuisineSelected ) {
                restaurant.getCuisines().add(cuisine);
            }
            cuisineIndex++;
            cuisine = getCellStringValue(cuisinesSheet, cuisineIndex, 0);
        }

        // Menu categories
        XSSFSheet menuCategoriesSheet = workbook.getSheet("Menu Categories");
        int menuCategoryIndex = 1;
        int emptyRowCount = 0;
        String menuCategoryName = getCellStringValue(menuCategoriesSheet,menuCategoryIndex,0);
        while(emptyRowCount < MAX_ALLOWED_EMPTY_ROWS ) {
            if(StringUtils.hasText(menuCategoryName)) {
                MenuCategory menuCategory = new MenuCategory();
                menuCategory.setName(menuCategoryName);
                menuCategory.setSummary(getCellStringValue(menuCategoriesSheet, menuCategoryIndex, 1));
                menuCategory.setType(getCellStringValue(menuCategoriesSheet, menuCategoryIndex, 2));
                menuCategory.setIconClass(getCellStringValue(menuCategoriesSheet, menuCategoryIndex, 3));
                restaurant.getMenu().getMenuCategories().add(menuCategory);
                menuCategoryValidator.validate(menuCategory,errors,menuCategoriesSheet.getSheetName(), menuCategoryIndex, 0);
                emptyRowCount = 0;
            }
            else {
                emptyRowCount++;
            }
            menuCategoryIndex++;
            menuCategoryName = getCellStringValue(menuCategoriesSheet,menuCategoryIndex,0);
        }

        // Menu items
        XSSFSheet menuItemsSheet = workbook.getSheet("Menu Items");
        int menuItemIndex = 0;
        emptyRowCount = 0;
        MenuItem currentMenuItem = null;
        String currentMenuCategoryName = null;
        while(emptyRowCount < MAX_ALLOWED_EMPTY_ROWS ) {
            menuItemIndex++;
            emptyRowCount++; // Will reset as soon as we find a string value anywhere in the row
            String menuItemName = getCellStringValue(menuItemsSheet, menuItemIndex, 2);
            if(StringUtils.hasText(menuItemName)) {
                emptyRowCount = 0; // Reset counter;
                if(currentMenuItem != null) {
                    MenuCategory menuCategory = restaurant.getMenu().getMenuCategory(currentMenuCategoryName);
                    if(menuCategory != null) {
                        menuCategory.getMenuItems().add(currentMenuItem);
                    }
                    menuItemValidator.validate(currentMenuItem, errors, menuItemsSheet.getSheetName(), menuItemIndex, 2);
                }
                currentMenuItem = new MenuItem(); // Create new menu item
                currentMenuCategoryName = getCellStringValue(menuItemsSheet,menuItemIndex,1);
                currentMenuItem.setTitle(menuItemName);
                currentMenuItem.setNumber((int)getCellDoubleValue(menuItemsSheet,menuItemIndex,0));
                currentMenuItem.setSubtitle(getCellStringValue(menuItemsSheet,menuItemIndex,3));
                currentMenuItem.setDescription(getCellStringValue(menuItemsSheet,menuItemIndex,4));
                currentMenuItem.setIconClass(getCellStringValue(menuItemsSheet,menuItemIndex,5));
                double cost = getCellDoubleValue(menuItemsSheet, menuItemIndex, 6);
                currentMenuItem.setCost(cost == 0d? null: cost);
                double defaultAdditionalItemCost = getCellDoubleValue(menuItemsSheet, menuItemIndex, 7);
                currentMenuItem.setAdditionalItemCost(defaultAdditionalItemCost == 0d? null: defaultAdditionalItemCost);
                double additionalItemChoiceLimit = getCellDoubleValue(menuItemsSheet, menuItemIndex, 8);
                currentMenuItem.setAdditionalItemChoiceLimit(additionalItemChoiceLimit == 0d? null: (int)additionalItemChoiceLimit);
                String subTypeName = getCellStringValue(menuItemsSheet,menuItemIndex,9);
                if(StringUtils.hasText(subTypeName)) {
                    MenuItemSubType subType = new MenuItemSubType();
                    subType.setType(subTypeName);
                    subType.setCost(getCellDoubleValue(menuItemsSheet,menuItemIndex,10));
                    currentMenuItem.getMenuItemSubTypes().add(subType);
                }
                String additionalItemChoiceName = getCellStringValue(menuItemsSheet,menuItemIndex,11);
                if(StringUtils.hasText(additionalItemChoiceName)) {
                    MenuItemAdditionalItemChoice choice = new MenuItemAdditionalItemChoice();
                    choice.setName(additionalItemChoiceName);
                    if(currentMenuItem.getAdditionalItemCost() == null ) {
                        choice.setCost(getCellDoubleValue(menuItemsSheet,menuItemIndex,12));
                    }
                    currentMenuItem.getAdditionalItemChoices().add(choice);
                }
                String typeName = getCellStringValue(menuItemsSheet,menuItemIndex,13);
                if( StringUtils.hasText(typeName)) {
                    MenuItemTypeCost typeCost = new MenuItemTypeCost();
                    typeCost.setType(typeName);
                    typeCost.setCost(getCellDoubleValue(menuItemsSheet,menuItemIndex,14));
                    typeCost.setAdditionalItemCost(getCellDoubleValue(menuItemsSheet,menuItemIndex,15));
                    currentMenuItem.getMenuItemTypeCosts().add(typeCost);
                }
            }
            else {
                // Just add any additional item details
                if(currentMenuItem != null) {
                    String subTypeName = getCellStringValue(menuItemsSheet,menuItemIndex,9);
                    if(StringUtils.hasText(subTypeName)) {
                        emptyRowCount = 0; // Reset counter;
                        MenuItemSubType subType = new MenuItemSubType();
                        subType.setType(subTypeName);
                        subType.setCost(getCellDoubleValue(menuItemsSheet,menuItemIndex,10));
                        currentMenuItem.getMenuItemSubTypes().add(subType);
                    }
                    String additionalItemChoiceName = getCellStringValue(menuItemsSheet,menuItemIndex,11);
                    if(StringUtils.hasText(additionalItemChoiceName)) {
                        emptyRowCount = 0; // Reset counter;
                        MenuItemAdditionalItemChoice choice = new MenuItemAdditionalItemChoice();
                        choice.setName(additionalItemChoiceName);
                        if(currentMenuItem.getAdditionalItemCost() == null ) {
                            choice.setCost(getCellDoubleValue(menuItemsSheet,menuItemIndex,12));
                        }
                        currentMenuItem.getAdditionalItemChoices().add(choice);
                    }
                    String typeName = getCellStringValue(menuItemsSheet,menuItemIndex,13);
                    if( StringUtils.hasText(typeName)) {
                        emptyRowCount = 0; // Reset counter;
                        MenuItemTypeCost typeCost = new MenuItemTypeCost();
                        typeCost.setType(typeName);
                        typeCost.setCost(getCellDoubleValue(menuItemsSheet,menuItemIndex,14));
                        typeCost.setAdditionalItemCost(getCellDoubleValue(menuItemsSheet,menuItemIndex,15));
                        currentMenuItem.getMenuItemTypeCosts().add(typeCost);
                    }
                }
            }
        }
        // Add last menu item
        if(currentMenuItem != null ) {
            MenuCategory menuCategory = restaurant.getMenu().getMenuCategory(currentMenuCategoryName);
            if(menuCategory != null) {
                menuCategory.getMenuItems().add(currentMenuItem);
            }
            menuItemValidator.validate(currentMenuItem, errors, menuItemsSheet.getSheetName(), menuItemIndex, 2);
        }

        // Discounts
        XSSFSheet discountSheet = workbook.getSheet("Discounts");
        int discountIndex = 0;
        emptyRowCount = 0;
        Discount currentDiscount = null;
        while(emptyRowCount < MAX_ALLOWED_EMPTY_ROWS ) {
            discountIndex++;
            emptyRowCount++; // Will reset as soon as we find a string value anywhere in the row
            String discountName = getCellStringValue(discountSheet, discountIndex, 0);
            if(StringUtils.hasText(discountName)) {
                emptyRowCount = 0; // Reset counter;
                if(currentDiscount != null) {
                    restaurant.getDiscounts().add(currentDiscount);
                }
                currentDiscount = new Discount(); // Create new discount
                currentDiscount.setTitle(discountName);
                currentDiscount.setDescription(getCellStringValue(discountSheet,discountIndex,1));
                currentDiscount.setDiscountType(getCellStringValue(discountSheet,discountIndex,2));
                currentDiscount.setDelivery("Y".equals(getCellStringValue(discountSheet,discountIndex,3)));
                currentDiscount.setCollection("Y".equals(getCellStringValue(discountSheet,discountIndex,4)));
                currentDiscount.setCanCombineWithOtherDiscounts("Y".equals(getCellStringValue(discountSheet,discountIndex,5)));
                double discount = getCellDoubleValue(discountSheet,discountIndex,6);
                currentDiscount.setDiscountAmount(discount == 0d? null: discount);
                double minimumOrderAmount = getCellDoubleValue(discountSheet,discountIndex,7);
                currentDiscount.setMinimumOrderValue(minimumOrderAmount == 0d? null: minimumOrderAmount);
                String freeItemChoiceName = getCellStringValue(discountSheet, discountIndex, 8);
                if(StringUtils.hasText(freeItemChoiceName)) {
                    currentDiscount.getFreeItems().add(freeItemChoiceName);
                }

                // Add applicable times for discount
                int applicableTimeAnchor = 9;
                List<ApplicableTime> applicableTimes = new ArrayList<ApplicableTime>();
                for(int i = 0; i < 7; i++ ) {
                    XSSFRow row = getRow(discountSheet,discountIndex);
                    ApplicableTime applicableTime = new ApplicableTime();
                    applicableTime.setDayOfWeek(i + 1);
                    applicableTime.setApplicable("Y".equals(getCellStringValue(discountSheet,discountIndex, applicableTimeAnchor + (3 * i))));
                    applicableTime.setApplicableFrom(getLocalTime(getCell(row,applicableTimeAnchor + (3 * i) + 1)));
                    applicableTime.setApplicableTo(getLocalTime(getCell(row, applicableTimeAnchor + (3 * i) + 2)));
                    applicableTimes.add(applicableTime);
                }
                currentDiscount.setDiscountApplicableTimes(applicableTimes);
            }
            else {
                // Just add any free item choices
                if(currentDiscount != null) {
                    String freeItemChoiceName = getCellStringValue(discountSheet, discountIndex, 8);
                    if(StringUtils.hasText(freeItemChoiceName)) {
                        emptyRowCount = 0; // Reset counter
                        currentDiscount.getFreeItems().add(freeItemChoiceName);
                    }
                }
            }
        }
        // Add last discount
        if(currentDiscount != null) {
            restaurant.getDiscounts().add(currentDiscount);
        }

        // Special offers
        XSSFSheet specialOffersSheet = workbook.getSheet("Special Offers");
        int specialOfferIndex = 1;
        emptyRowCount = 0;
        String specialOfferName = getCellStringValue(specialOffersSheet,specialOfferIndex,1);
        while(emptyRowCount < MAX_ALLOWED_EMPTY_ROWS ) {
            if(StringUtils.hasText(specialOfferName)) {
                SpecialOffer specialOffer = new SpecialOffer();
                specialOffer.setTitle(specialOfferName);
                specialOffer.setNumber((int)getCellDoubleValue(specialOffersSheet,specialOfferIndex,0));
                specialOffer.setDescription(getCellStringValue(specialOffersSheet,specialOfferIndex,2));
                double cost = getCellDoubleValue(specialOffersSheet,specialOfferIndex,3);
                specialOffer.setCost(cost == 0d?null: cost);

                // Add applicable times for special offer
                int applicableTimeAnchor = 4;
                List<ApplicableTime> applicableTimes = new ArrayList<ApplicableTime>();
                for(int i = 0; i < 7; i++ ) {
                    XSSFRow row = getRow(specialOffersSheet,specialOfferIndex);
                    ApplicableTime applicableTime = new ApplicableTime();
                    applicableTime.setDayOfWeek(i + 1);
                    applicableTime.setApplicable("Y".equals(getCellStringValue(specialOffersSheet,specialOfferIndex, applicableTimeAnchor + (3 * i))));
                    applicableTime.setApplicableFrom(getLocalTime(getCell(row,applicableTimeAnchor + (3 * i) + 1)));
                    applicableTime.setApplicableTo(getLocalTime(getCell(row, applicableTimeAnchor + (3 * i) + 2)));
                    applicableTimes.add(applicableTime);
                }
                specialOffer.setOfferApplicableTimes(applicableTimes);
                emptyRowCount = 0;
            }
            else {
                emptyRowCount++;
            }
            specialOfferIndex++;
            specialOfferName = getCellStringValue(specialOffersSheet,specialOfferIndex,0);
        }

        // Special offer items
        XSSFSheet specialOfferItemsSheet = workbook.getSheet("Special Offer Items");
        int specialOfferItemIndex = 0;
        emptyRowCount = 0;
        SpecialOfferItem currentSpecialOfferItem = null;
        String currentSpecialOfferName = null;
        while(emptyRowCount < MAX_ALLOWED_EMPTY_ROWS ) {
            specialOfferItemIndex++;
            emptyRowCount++; // Will reset as soon as we find a string value anywhere in the row
            String specialOfferItemName = getCellStringValue(specialOfferItemsSheet, specialOfferIndex, 1);
            if(StringUtils.hasText(specialOfferItemName)) {
                emptyRowCount = 0; // Reset counter;
                if(currentSpecialOfferItem != null) {
                    SpecialOffer specialOffer = restaurant.getSpecialOfferByTitle(currentSpecialOfferName);
                    if(specialOffer != null) {
                        specialOffer.getSpecialOfferItems().add(currentSpecialOfferItem);
                    }
                }
                currentSpecialOfferItem = new SpecialOfferItem(); // Create new special offer item
                currentSpecialOfferName = getCellStringValue(specialOfferItemsSheet,specialOfferItemIndex,0);
                currentSpecialOfferItem.setTitle(specialOfferName);
                currentSpecialOfferItem.setDescription(getCellStringValue(specialOfferItemsSheet,specialOfferItemIndex,2));
                String choiceName = getCellStringValue(specialOfferItemsSheet,specialOfferItemIndex,3);
                if(StringUtils.hasText(choiceName)) {
                    currentSpecialOfferItem.getSpecialOfferItemChoices().add(choiceName);
                }
            }
            else {
                // Just add any additional choices
                if(currentSpecialOfferItem != null) {
                    String choiceName = getCellStringValue(specialOfferItemsSheet,specialOfferItemIndex,3);
                    if(StringUtils.hasText(choiceName)) {
                        emptyRowCount = 0; // Reset counter
                        currentSpecialOfferItem.getSpecialOfferItemChoices().add(choiceName);
                    }
                }
            }
        }
        // Add last special offer item
        if(currentSpecialOfferItem != null ) {
            SpecialOffer specialOffer = restaurant.getSpecialOfferByTitle(currentSpecialOfferName);
            if(specialOffer != null) {
                specialOffer.getSpecialOfferItems().add(currentSpecialOfferItem);
            }
        }

        return restaurant;
    }


    /**
     * @param cell
     * @return
     */

    private LocalTime getLocalTime(XSSFCell cell) {
        String value = cell.getStringCellValue();
        if( !StringUtils.hasText(value)) {
            return null;
        }
        try {
            return timeFormatter.parseLocalTime(value);            
        }
        catch( Exception ex ) {
            return null;
        }
    }


    /**
     * @param cell
     * @return
     */
    @SuppressWarnings("deprecation")
    private LocalDate getLocalDate(XSSFCell cell) {
        Date value = cell.getDateCellValue();
        if( value == null ) { 
            return null;
        }
        return new LocalDate(value.getTime());
    }

}
