package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.web.controller.helper.ExcelUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ExcelUploadController {

    private static final Logger LOGGER = Logger.getLogger(ExcelUploadController.class);
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CuisineProvider cuisineProvider;

    @Autowired
    private JSONUtils jsonUtils;
    
    private final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

    @ResponseBody
    @RequestMapping(value="/admin/menu/upload.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> uploadRestaurant(@RequestParam("file") CommonsMultipartFile file) throws Exception {

        LOGGER.info("Uploading workboox");
        Map<String,Object> model = new HashMap<String,Object>();
        
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

            // Build restaurant from workbook
            Restaurant restaurant = buildRestaurantFromWorkbook(workbook);

            // All OK
            model.put("success",true);
        }
        catch( Exception ex ) {
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
     * @param workbook
     * @return
     */
    
    private Restaurant buildRestaurantFromWorkbook(XSSFWorkbook workbook) {
        Restaurant restaurant = new Restaurant();

        // Core data
        restaurant.setRestaurantId(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Id"));
        restaurant.setName(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Name"));
        restaurant.setDescription(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Description"));

        // Address
        restaurant.getAddress().setAddress1(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Address.Address1"));
        restaurant.getAddress().setTown(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Address.Town"));
        restaurant.getAddress().setRegion(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Address.Region"));
        restaurant.getAddress().setPostCode(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Address.Postcode"));
        
        // Contact details
        restaurant.setContactTelephone(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Contact.Telephone"));
        restaurant.setContactMobile(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Contact.Mobile"));
        restaurant.setContactEmail(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Contact.Email"));
        restaurant.setWebsite(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Contact.Website"));

        // Main contact
        restaurant.getMainContact().setFirstName(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.MainContact.FirstName"));
        restaurant.getMainContact().setLastName(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.MainContact.LastName"));
        restaurant.getMainContact().setTelephone(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.MainContact.Telephone"));
        restaurant.getMainContact().setEmail(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.MainContact.Email"));

        // Notification details
        restaurant.getNotificationOptions().setReceiveNotificationCall("Y".equals(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Notification.ReceiveCall")));
        restaurant.getNotificationOptions().setReceiveSMSNotification("Y".equals(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Notification.ReceiveSMS")));
        restaurant.getNotificationOptions().setNotificationPhoneNumber(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Notification.Telephone"));
        restaurant.getNotificationOptions().setNotificationSMSNumber(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Notification.SMS"));
        restaurant.getNotificationOptions().setNotificationEmailAddress(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Notification.Email"));
        restaurant.getNotificationOptions().setPrinterEmailAddress(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Notification.PrinterEmail"));

        // Administration
        restaurant.setListOnSite("Y".equals(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.IncludeOnSite")));
        restaurant.setPhoneOrdersOnly("Y".equals(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.PhoneOrdersOnly")));
        restaurant.setRecommended("Y".equals(ExcelUtils.getNamedCellStringValue(workbook, "Restaurant.Recommended")));
        restaurant.setSearchRanking((int)ExcelUtils.getNamedCellDoubleValue(workbook, "Restaurant.SearchRanking"));
        restaurant.setCommissionPercent(ExcelUtils.getNamedCellDoubleValue(workbook, "Restaurant.Commission"));









        return restaurant;
    }

}
