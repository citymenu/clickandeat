package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.ezar.clickandeat.web.controller.helper.ExcelUtils.*;

@Controller
public class ExcelController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CuisineProvider cuisineProvider;
    
    private String templatePath = "/template/MenuTemplate.xlsx";

    private final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

    @ResponseBody
    @RequestMapping(value="/admin/menu/downloadTemplate.html", method = RequestMethod.GET )
    public ResponseEntity<byte[]> downloadTemplate(HttpServletRequest request) throws Exception {
        Resource resource = new ClassPathResource(templatePath);
        XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());
        Map<String,CellStyle> styles = generateCellStyles(workbook);
        XSSFSheet cuisineSheet = workbook.getSheet("Cuisines");
        int cuisineIndex = 1;
        for( String cuisine: cuisineProvider.getCuisineList()) {
            XSSFRow row = getRow(cuisineSheet, cuisineIndex++);
            createCell(row, 0, Cell.CELL_TYPE_STRING, styles.get("boldtext")).setCellValue(cuisine);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set("Content-Disposition","attachment;Filename=RestaurantDataTemplate.xlsx");
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value="/admin/menu/downloadMenu.html", method = RequestMethod.GET )
    public ResponseEntity<byte[]> downloadMenu(@RequestParam(value="id") String restaurantId, HttpServletRequest request) throws Exception {

        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);

        Resource resource = new ClassPathResource(templatePath);
        XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());

        // Build cell styles
        Map<String,CellStyle> styles = generateCellStyles(workbook);

        // Write out main details of restaurant
        writeToNamedCell(workbook, "Restaurant.Id", restaurant.getRestaurantId());
        writeToNamedCell(workbook, "Restaurant.Name",restaurant.getName());
        writeToNamedCell(workbook, "Restaurant.Description",restaurant.getDescription());

        // Write out admin details of restaurant
        writeToNamedCell(workbook, "Restaurant.IncludeOnSite", restaurant.getListOnSite()?"Y":"N");
        writeToNamedCell(workbook, "Restaurant.PhoneOrdersOnly", restaurant.getPhoneOrdersOnly()?"Y":"N");
        writeToNamedCell(workbook, "Restaurant.Recommended", restaurant.getRecommended()?"Y":"N");
        writeToNamedCell(workbook, "Restaurant.SearchRanking", restaurant.getSearchRanking());
        writeToNamedCell(workbook, "Restaurant.Commission", restaurant.getCommissionPercent());

        // Write out address
        writeToNamedCell(workbook, "Restaurant.Address.Address1",restaurant.getAddress().getAddress1());
        writeToNamedCell(workbook, "Restaurant.Address.Town",restaurant.getAddress().getTown());
        writeToNamedCell(workbook, "Restaurant.Address.Region",restaurant.getAddress().getRegion());
        writeToNamedCell(workbook, "Restaurant.Address.Postcode",restaurant.getAddress().getPostCode());

        // Write out restaurant contact details
        writeToNamedCell(workbook, "Restaurant.Contact.Telephone",restaurant.getContactTelephone());
        writeToNamedCell(workbook, "Restaurant.Contact.Mobile",restaurant.getContactMobile());
        writeToNamedCell(workbook, "Restaurant.Contact.Email",restaurant.getContactEmail());
        writeToNamedCell(workbook, "Restaurant.Contact.Website",restaurant.getWebsite());

        // Write out restaurant main contact details
        writeToNamedCell(workbook, "Restaurant.MainContact.FirstName",restaurant.getMainContact().getFirstName());
        writeToNamedCell(workbook, "Restaurant.MainContact.LastName",restaurant.getMainContact().getLastName());
        writeToNamedCell(workbook, "Restaurant.MainContact.Telephone",restaurant.getMainContact().getTelephone());
        writeToNamedCell(workbook, "Restaurant.MainContact.Email",restaurant.getMainContact().getEmail());

        // Write out notification details
        writeToNamedCell(workbook, "Restaurant.Notification.ReceiveCall",restaurant.getNotificationOptions().isReceiveNotificationCall()?"Y":"N");
        writeToNamedCell(workbook, "Restaurant.Notification.ReceiveSMS",restaurant.getNotificationOptions().isReceiveSMSNotification()?"Y":"N");
        writeToNamedCell(workbook, "Restaurant.Notification.Telephone",restaurant.getNotificationOptions().getNotificationPhoneNumber());
        writeToNamedCell(workbook, "Restaurant.Notification.SMS",restaurant.getNotificationOptions().getNotificationSMSNumber());
        writeToNamedCell(workbook, "Restaurant.Notification.Email",restaurant.getNotificationOptions().getNotificationEmailAddress());
        writeToNamedCell(workbook, "Restaurant.Notification.PrinterEmail",restaurant.getNotificationOptions().getPrinterEmailAddress());

        // Write out delivery options
        XSSFSheet deliveryDetailsSheet = workbook.getSheet("Delivery Details");
        writeToNamedCell(workbook, "Restaurant.Delivery.CollectionOnly",restaurant.getDeliveryOptions().isCollectionOnly()?"Y":"N");
        writeToNamedCell(workbook, "Restaurant.Delivery.DeliveryTime",restaurant.getDeliveryOptions().getDeliveryTimeMinutes());
        writeToNamedCell(workbook, "Restaurant.Delivery.CollectionTime",restaurant.getDeliveryOptions().getCollectionTimeMinutes());
        writeToNamedCell(workbook, "Restaurant.Delivery.Charge",restaurant.getDeliveryOptions().getDeliveryCharge());
        writeToNamedCell(workbook, "Restaurant.Delivery.AllowFreeDelivery",restaurant.getDeliveryOptions().isAllowFreeDelivery()?"Y":"N");
        writeToNamedCell(workbook, "Restaurant.Delivery.MinimumOrderForDelivery",restaurant.getDeliveryOptions().getMinimumOrderForDelivery());
        writeToNamedCell(workbook, "Restaurant.Delivery.AllowOrdersBelowMinimumForFreeDelivery",restaurant.getDeliveryOptions().isAllowDeliveryBelowMinimumForFreeDelivery()?"Y":"N");
        writeToNamedCell(workbook, "Restaurant.Delivery.DeliveryRadius",restaurant.getDeliveryOptions().getDeliveryRadiusInKilometres());
        writeToNamedCell(workbook, "Restaurant.Delivery.AreasDeliveredTo", StringUtils.collectionToCommaDelimitedString(restaurant.getDeliveryOptions().getAreasDeliveredTo()));

        // Write out areas with same delivery charge
        XSSFCell sameDeliveryChargeAnchor = getFirstCell(workbook, "Restaurant.Delivery.SameDeliveryCharge");
        int sameDeliveryChargeRow = sameDeliveryChargeAnchor.getRowIndex();
        int sameDeliveryChargeCol = sameDeliveryChargeAnchor.getColumnIndex();
        for( int i = 0; i < restaurant.getDeliveryOptions().getAreaDeliveryCharges().size(); i++ ) {
            AreaDeliveryCharge charge = restaurant.getDeliveryOptions().getAreaDeliveryCharges().get(i);
            XSSFRow row = getRow(deliveryDetailsSheet, sameDeliveryChargeRow + i );
            createCell(row, sameDeliveryChargeCol, Cell.CELL_TYPE_NUMERIC, styles.get("text")).setCellValue(StringUtils.collectionToCommaDelimitedString(charge.getAreas()));
            if( charge.getDeliveryCharge() != null ) {
                createCell(row, sameDeliveryChargeCol + 1, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(charge.getDeliveryCharge());
            }
        }

        // Write out areas with same minimum order for delivery
        XSSFCell minDeliveryChargeAnchor = getFirstCell(workbook, "Restaurant.Delivery.SameMinimumOrder");
        int minDeliveryChargeRow = minDeliveryChargeAnchor.getRowIndex();
        int minDeliveryChargeCol = minDeliveryChargeAnchor.getColumnIndex();
        for( int i = 0; i < restaurant.getDeliveryOptions().getAreaMinimumOrderCharges().size(); i++ ) {
            AreaDeliveryCharge charge = restaurant.getDeliveryOptions().getAreaMinimumOrderCharges().get(i);
            XSSFRow row = getRow(deliveryDetailsSheet, minDeliveryChargeRow + i);
            createCell(row, minDeliveryChargeCol, Cell.CELL_TYPE_NUMERIC, styles.get("text")).setCellValue(StringUtils.collectionToCommaDelimitedString(charge.getAreas()));
            if( charge.getDeliveryCharge() != null ) {
                createCell(row, minDeliveryChargeCol + 1, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(charge.getDeliveryCharge());
            }
        }

        // Write out delivery charges by minimum order amount
        XSSFCell deliveryChargesByOrderAmountAnchor = getFirstCell(workbook, "Restaurant.Delivery.DeliveryChargesByOrderAmount");
        int deliveryChargesByOrderAmountRow = deliveryChargesByOrderAmountAnchor.getRowIndex();
        int deliveryChargesByOrderAmountCol = deliveryChargesByOrderAmountAnchor.getColumnIndex();
        int variableDeliveryChargeIndex = 0;
        for( VariableDeliveryCharge charge: restaurant.getDeliveryOptions().getVariableDeliveryCharges() ) {
            XSSFRow row = getRow(deliveryDetailsSheet, deliveryChargesByOrderAmountRow + variableDeliveryChargeIndex);
            if( charge.getMinimumOrderValue() != null ) {
                createCell(row, deliveryChargesByOrderAmountCol, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(charge.getMinimumOrderValue());
            }
            if( charge.getDeliveryCharge() != null ) {
                createCell(row, deliveryChargesByOrderAmountCol + 1, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(charge.getDeliveryCharge());
            }
        }

        // Export opening times
        XSSFSheet openingTimesSheet = workbook.getSheet("Opening Times");
        OpeningTimes restaurantOpeningTimes = restaurant.getOpeningTimes();
        AreaReference openingTimesAreaReference = new AreaReference(workbook.getName("Restaurant.OpeningTimes").getRefersToFormula());
        CellReference openingTimesCellReference = openingTimesAreaReference.getFirstCell();
        int openingTimesRow = openingTimesCellReference.getRow();
        int openingTimesColumn = (int)openingTimesCellReference.getCol();
        for( OpeningTime openingTime: restaurantOpeningTimes.getOpeningTimes() ) {
            int dayOfWeek = openingTime.getDayOfWeek();
            XSSFRow row = openingTimesSheet.getRow(openingTimesRow + (dayOfWeek - 1));
            createCell(row, openingTimesColumn, Cell.CELL_TYPE_STRING, styles.get("plain")).setCellValue(openingTime.isOpen()?"Y":"N");
            if( openingTime.getEarlyOpeningTime() != null ) {
                createCell(row, openingTimesColumn + 1, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(openingTime.getEarlyOpeningTime()));
            }
            if( openingTime.getEarlyClosingTime() != null ) {
                createCell(row, openingTimesColumn + 2, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(openingTime.getEarlyClosingTime()));
            }
            if( openingTime.getLateOpeningTime() != null ) {
                createCell(row, openingTimesColumn + 3, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(openingTime.getLateOpeningTime()));
            }
            if( openingTime.getLateOpeningTime() != null ) {
                createCell(row, openingTimesColumn + 4, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(openingTime.getLateClosingTime()));
            }
        }

        // Output bank holiday opening times
        OpeningTime bankHolidayOpeningTime = restaurantOpeningTimes.getBankHolidayOpeningTimes();
        if( bankHolidayOpeningTime != null ) {
            XSSFRow row = openingTimesSheet.getRow(openingTimesRow + 7);
            createCell(row, openingTimesColumn, Cell.CELL_TYPE_STRING, styles.get("plain")).setCellValue(bankHolidayOpeningTime.isOpen()?"Y":"N");
            if( bankHolidayOpeningTime.getEarlyOpeningTime() != null ) {
                createCell(row, openingTimesColumn + 1, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(bankHolidayOpeningTime.getEarlyOpeningTime()));
            }
            if( bankHolidayOpeningTime.getEarlyClosingTime() != null ) {
                createCell(row, openingTimesColumn + 1, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(bankHolidayOpeningTime.getEarlyClosingTime()));
            }
            if( bankHolidayOpeningTime.getLateOpeningTime() != null ) {
                createCell(row, openingTimesColumn + 2, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(bankHolidayOpeningTime.getLateOpeningTime()));
            }
            if( bankHolidayOpeningTime.getLateOpeningTime() != null ) {
                createCell(row, openingTimesColumn + 3, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(bankHolidayOpeningTime.getLateClosingTime()));
            }
        }

        // Output closed dates
        CellReference closedDatesCellReference = new CellReference(workbook.getName("ClosedDates").getRefersToFormula());
        int closedDatesRow = closedDatesCellReference.getRow();
        int closedDateIndex = 1;
        for(LocalDate closedDate: restaurant.getOpeningTimes().getClosedDates()) {
            XSSFRow row = openingTimesSheet.createRow(closedDatesRow + closedDateIndex++ );
            createCell(row, 0, Cell.CELL_TYPE_STRING, styles.get("date")).setCellValue(dateFormatter.print(closedDate));
        }

        // Export cuisines
        XSSFSheet cuisinesSheet = workbook.getSheet("Cuisines");
        int cuisineIndex = 1;
        for( String cuisine: cuisineProvider.getCuisineList()) {
            XSSFRow row = getRow(cuisinesSheet, cuisineIndex++);
            createCell(row, 0, Cell.CELL_TYPE_STRING, styles.get("boldtext")).setCellValue(cuisine);
            createCell(row, 1, Cell.CELL_TYPE_STRING, styles.get("plain")).setCellValue(restaurant.getCuisines().contains(cuisine)?"Y":"N");
        }
        
        // Export the menu
        Menu menu = restaurant.getMenu();
        XSSFSheet categorySheet = workbook.getSheet("Menu Categories");
        XSSFSheet itemSheet = workbook.getSheet("Menu Items");
        
        int categoryIndex = 1;
        int itemIndex = 1;
        for(MenuCategory category: menu.getMenuCategories()) {
            
            // Output menu category onto category sheet
            XSSFRow categoryRow = categorySheet.createRow(categoryIndex++);
            createCell(categoryRow, 0, Cell.CELL_TYPE_STRING, styles.get("boldtext")).setCellValue(category.getName());
            createCell(categoryRow, 1, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(category.getSummary());
            createCell(categoryRow, 2, Cell.CELL_TYPE_STRING, styles.get("plain")).setCellValue(category.getType());
            createCell(categoryRow, 3, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(category.getIconClass());
            
            // Output menu items onto second sheet
            for(MenuItem menuItem: category.getMenuItems()) {
                int rowCount = getRowCount(menuItem); // Number of rows needed for this item
                for( int rowIndex = 0; rowIndex < rowCount; rowIndex ++ ) {
                    XSSFRow itemRow = itemSheet.createRow(itemIndex);

                    // Output main detail row for menu item
                    if(rowIndex == 0 ) {
                        if( menuItem.getNumber() != 0 ) {
                            createCell(itemRow, 0, Cell.CELL_TYPE_NUMERIC, styles.get("number")).setCellValue(menuItem.getNumber());
                        }
                        createCell(itemRow, 1, Cell.CELL_TYPE_STRING, styles.get("boldtext")).setCellValue(category.getName());
                        createCell(itemRow, 2, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(menuItem.getTitle());
                        createCell(itemRow, 3, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(menuItem.getSubtitle());
                        createCell(itemRow, 4, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(menuItem.getDescription());
                        createCell(itemRow, 5, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(menuItem.getIconClass());
                        if( menuItem.getCost() != null ) {
                            createCell(itemRow, 6, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(menuItem.getCost());
                        }
                        if( menuItem.getAdditionalItemCost() != null ) {
                            createCell(itemRow, 7, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(menuItem.getAdditionalItemCost());
                        }
                        if( menuItem.getAdditionalItemChoiceLimit() != null ) {
                            createCell(itemRow, 8, Cell.CELL_TYPE_NUMERIC, styles.get("number")).setCellValue(menuItem.getAdditionalItemChoiceLimit());
                        }
                    }

                    // Output rest of details
                    if( menuItem.getMenuItemSubTypes().size() > rowIndex ) {
                        MenuItemSubType subType = menuItem.getMenuItemSubTypes().get(rowIndex);
                        createCell(itemRow, 9, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(subType.getType());
                        if( subType.getCost() != null ) {
                            createCell(itemRow, 10, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(subType.getCost());
                        }
                    }

                    if( menuItem.getAdditionalItemChoices().size() > rowIndex ) {
                        MenuItemAdditionalItemChoice choice = menuItem.getAdditionalItemChoices().get(rowIndex);
                        createCell(itemRow, 11, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(choice.getName());
                        if( choice.getCost() != null ) {
                            createCell(itemRow, 12, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(choice.getCost());
                        }
                    }

                    if( menuItem.getMenuItemTypeCosts().size() > rowIndex ) {
                        MenuItemTypeCost cost = menuItem.getMenuItemTypeCosts().get(rowIndex);
                        createCell(itemRow, 13, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(cost.getType());
                        if( cost.getCost() != null ) {
                            createCell(itemRow, 14, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(cost.getCost());
                        }
                        if( cost.getAdditionalItemCost() != null ) {
                            createCell(itemRow, 15, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(cost.getAdditionalItemCost());
                        }
                    }

                    itemIndex ++;
                }

                // Add a 2-line separator
                for( int i = 0; i < 2; i++ ) {
                    itemSheet.createRow(itemIndex++);
                }
            }
        }
        
        // Export discounts
        XSSFSheet discountSheet = workbook.getSheet("Discounts");
        int discountIndex = 1;
        for( Discount discount: restaurant.getDiscounts()) {
            int rowCount = Math.max(discount.getFreeItems().size(),1);
            for( int rowIndex = 0; rowIndex < rowCount; rowIndex++ ) {
                XSSFRow row = discountSheet.createRow(discountIndex);
                if( rowIndex == 0 ) {
                    if(discount.getTitle() != null ) {
                        createCell(row, 0, Cell.CELL_TYPE_STRING, styles.get("boldtext")).setCellValue(discount.getTitle());
                    }
                    if(discount.getDescription() != null ) {
                        createCell(row, 1, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(discount.getDescription());
                    }
                    if(discount.getDiscountType() != null) {
                        createCell(row, 2, Cell.CELL_TYPE_STRING, styles.get("plain")).setCellValue(discount.getDiscountType());
                    }
                    createCell(row, 3, Cell.CELL_TYPE_STRING, styles.get("center")).setCellValue(discount.isDelivery()?"Y":"N");
                    createCell(row, 4, Cell.CELL_TYPE_STRING, styles.get("center")).setCellValue(discount.isCollection()?"Y":"N");
                    createCell(row, 5, Cell.CELL_TYPE_STRING, styles.get("center")).setCellValue(discount.isCanCombineWithOtherDiscounts()?"Y":"N");
                    if(discount.getDiscountAmount() != null) {
                        createCell(row, 6, Cell.CELL_TYPE_NUMERIC, styles.get("number")).setCellValue(discount.getDiscountAmount());
                    }
                    if(discount.getMinimumOrderValue() != null) {
                        createCell(row, 7, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(discount.getMinimumOrderValue());
                    }

                    // Add applicable times for discounts
                    int applicableTimeCol = 9;
                    for(ApplicableTime applicableTime: discount.getDiscountApplicableTimes()) {
                        int dayOfWeekOffset = (applicableTime.getDayOfWeek() -1 ) * 3;
                        createCell(row, applicableTimeCol + dayOfWeekOffset, Cell.CELL_TYPE_STRING, styles.get("center")).setCellValue(applicableTime.getApplicable()?"Y":"N");
                        if( applicableTime.getApplicableFrom() != null ) {
                            createCell(row, applicableTimeCol + dayOfWeekOffset + 1, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(applicableTime.getApplicableFrom()));
                        }
                        if( applicableTime.getApplicableTo() != null ) {
                            createCell(row, applicableTimeCol + dayOfWeekOffset + 2, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(applicableTime.getApplicableTo()));
                        }
                    }
                }

                // List out free items
                if( discount.getFreeItems().size() > rowIndex ) {
                    createCell(row, 8, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(discount.getFreeItems().get(rowIndex));
                }

                discountIndex++;
            }

            // Add a 2-line separator
            for( int i = 0; i < 2; i++ ) {
                discountSheet.createRow(discountIndex++);
            }
        }

        // Export special offers
        XSSFSheet specialOfferSheet = workbook.getSheet("Special Offers");
        XSSFSheet specialOfferItemSheet = workbook.getSheet("Special Offer Items");

        int specialOfferIndex = 1;
        int specialOfferItemIndex = 1;

        for(SpecialOffer specialOffer: restaurant.getSpecialOffers()) {

            // Output special offer onto special offer sheet
            XSSFRow specialOfferRow = specialOfferSheet.createRow(specialOfferIndex++);
            if( specialOffer.getNumber() != 0 ) {
                createCell(specialOfferRow, 0, Cell.CELL_TYPE_NUMERIC, styles.get("number")).setCellValue(specialOffer.getNumber());
            }
            createCell(specialOfferRow, 1, Cell.CELL_TYPE_STRING, styles.get("boldtext")).setCellValue(specialOffer.getTitle());
            createCell(specialOfferRow, 2, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(specialOffer.getDescription());
            if( specialOffer.getCost() != null ) {
                createCell(specialOfferRow, 3, Cell.CELL_TYPE_NUMERIC, styles.get("currency")).setCellValue(specialOffer.getCost());
            }

            // Add applicable times for special offer
            int applicableTimeCol = 4;
            for(ApplicableTime applicableTime: specialOffer.getOfferApplicableTimes()) {
                int dayOfWeekOffset = (applicableTime.getDayOfWeek() -1 ) * 3;
                createCell(specialOfferRow, applicableTimeCol + dayOfWeekOffset, Cell.CELL_TYPE_STRING, styles.get("center")).setCellValue(applicableTime.getApplicable()?"Y":"N");
                if( applicableTime.getApplicableFrom() != null ) {
                    createCell(specialOfferRow, applicableTimeCol + dayOfWeekOffset + 1, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(applicableTime.getApplicableFrom()));
                }
                if( applicableTime.getApplicableTo() != null ) {
                    createCell(specialOfferRow, applicableTimeCol + dayOfWeekOffset + 2, Cell.CELL_TYPE_STRING, styles.get("time")).setCellValue(timeFormatter.print(applicableTime.getApplicableTo()));
                }
            }

            // Output special offer items onto second sheet
            for(SpecialOfferItem specialOfferItem: specialOffer.getSpecialOfferItems()) {
                int rowCount = Math.max(1,specialOfferItem.getSpecialOfferItemChoices().size()); // Number of rows needed for this item
                for( int rowIndex = 0; rowIndex < rowCount; rowIndex ++ ) {
                    XSSFRow specialOfferItemRow = specialOfferItemSheet.createRow(specialOfferItemIndex);

                    // Output main detail row for special offer item
                    if(rowIndex == 0 ) {
                        createCell(specialOfferItemRow, 0, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(specialOffer.getTitle());
                        createCell(specialOfferItemRow, 1, Cell.CELL_TYPE_STRING, styles.get("boldtext")).setCellValue(specialOfferItem.getTitle());
                        createCell(specialOfferItemRow, 2, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(specialOfferItem.getDescription());
                    }

                    // Output rest of details
                    if( specialOfferItem.getSpecialOfferItemChoices().size() > rowIndex ) {
                        String specialOfferItemChoice = specialOfferItem.getSpecialOfferItemChoices().get(rowIndex);
                        createCell(specialOfferItemRow, 3, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(specialOfferItemChoice);
                    }

                    specialOfferItemIndex++;
                }

                // Add a 2-line separator
                for( int i = 0; i < 2; i++ ) {
                    specialOfferItemSheet.createRow(specialOfferItemIndex++);
                }
            }
        }


        // Return workbook to brower
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set("Content-Disposition","attachment;Filename=" + restaurantId + "_" + restaurant.getName() + ".xlsx");
        headers.setCacheControl("no-cache");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.OK);
    }


    /**
     * @param menuItem
     * @return
     */

    private int getRowCount(MenuItem menuItem) {
        int rowCount = 1; // Default
        rowCount = Math.max(rowCount, menuItem.getAdditionalItemChoices().size());
        rowCount = Math.max(rowCount,  menuItem.getMenuItemSubTypes().size());
        rowCount = Math.max(rowCount, menuItem.getMenuItemTypeCosts().size());
        return rowCount;
    }


    /**
     * @param workbook
     * @return
     */

    private Map<String,CellStyle> generateCellStyles(XSSFWorkbook workbook) {
        Map<String,CellStyle> styles = new HashMap<String,CellStyle>();

        CreationHelper createHelper = workbook.getCreationHelper();
        
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short)10);

        XSSFFont bold = workbook.createFont();
        bold.setFontHeightInPoints((short)10);
        bold.setBold(true);

        CellStyle plain = workbook.createCellStyle();
        plain.setFont(font);
        plain.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put("plain",plain);
        
        CellStyle text = workbook.createCellStyle();
        text.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        text.setFont(font);
        text.setWrapText(true);
        styles.put("text",text);

        CellStyle boldText = workbook.createCellStyle();
        boldText.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        boldText.setFont(bold);
        boldText.setWrapText(true);
        styles.put("boldtext",boldText);

        CellStyle center = workbook.createCellStyle();
        center.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        center.setAlignment(CellStyle.ALIGN_CENTER);
        center.setFont(font);
        styles.put("center",center);

        CellStyle number = workbook.createCellStyle();
        number.setFont(font);
        number.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        number.setDataFormat(createHelper.createDataFormat().getFormat("0"));
        styles.put("number",number);
        
        CellStyle currency = workbook.createCellStyle();
        currency.setFont(font);
        currency.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        currency.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00 [$€-1]"));
        styles.put("currency",currency);

        CellStyle separator = workbook.createCellStyle();
        separator.setFont(font);
        separator.setBorderBottom(CellStyle.BORDER_DASHED);
        styles.put("separator",separator);

        CellStyle date = workbook.createCellStyle();
        date.setFont(font);
        date.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        date.setAlignment(CellStyle.ALIGN_CENTER);
        date.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        styles.put("date",date);

        CellStyle time = workbook.createCellStyle();
        time.setFont(font);
        time.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        time.setAlignment(CellStyle.ALIGN_CENTER);
        time.setDataFormat(createHelper.createDataFormat().getFormat("hh:mm"));
        styles.put("time",time);

        return styles;
    }

}
