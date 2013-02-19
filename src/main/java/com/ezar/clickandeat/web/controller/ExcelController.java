package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ExcelController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    private String templatePath = "/template/MenuTemplate.xlsx";


    @ResponseBody
    @RequestMapping(value="/admin/menu/downloadTemplate.html", method = RequestMethod.GET )
    public ResponseEntity<byte[]> downloadTemplate(HttpServletRequest request) throws Exception {
        Resource resource = new ClassPathResource(templatePath);
        InputStream is = resource.getInputStream();
        byte[] buff = new byte[1024];
        int bytesRead = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((bytesRead = is.read(buff)) != -1) {
            baos.write(buff, 0, bytesRead);
        }
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set("Content-Disposition","attachment;Filename=MenuTemplate.xlsx");
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value="/admin/menu/downloadMenu.html", method = RequestMethod.GET )
    public ResponseEntity<byte[]> downloadMenu(@RequestParam(value="id") String restaurantId, HttpServletRequest request) throws Exception {

        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
        Menu menu = restaurant.getMenu();

        Resource resource = new ClassPathResource(templatePath);
        XSSFWorkbook workbook = new XSSFWorkbook(resource.getInputStream());
        XSSFSheet categorySheet = workbook.getSheet("Categories");
        XSSFSheet itemSheet = workbook.getSheet("Items");
        
        // Build cell styles
        Map<String,CellStyle> styles = generateCellStyles(workbook);

        int categoryIndex = 1;
        int itemIndex = 1;
        for(MenuCategory category: menu.getMenuCategories()) {
            
            // Output menu category onto first sheet
            XSSFRow categoryRow = categorySheet.createRow(categoryIndex++);
            createCell(categoryRow, 0, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(category.getName());
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
                        createCell(itemRow, 1, Cell.CELL_TYPE_STRING, styles.get("text")).setCellValue(category.getName());
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

                // Add a menu item separator row
                XSSFRow separatorRow = itemSheet.createRow(itemIndex++);
                for( int i = 0; i < 16; i++ ) {
                    createCell(separatorRow, i, Cell.CELL_TYPE_BLANK, styles.get("separator"));
                }
                
            }
        }
        
        // Return workbook to brower
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set("Content-Disposition","attachment;Filename=" + restaurantId + ".xlsx");
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
        
        CellStyle plain = workbook.createCellStyle();
        plain.setFont(font);
        plain.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put("plain",plain);
        
        CellStyle text = workbook.createCellStyle();
        text.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        text.setFont(font);
        text.setWrapText(true);
        styles.put("text",text);
        
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

        return styles;
    }


    /**
     * @param row
     * @param column
     * @param cellType
     * @return
     */

    private XSSFCell createCell(XSSFRow row, int column, int cellType, CellStyle cellStyle ) {
        XSSFCell cell = row.createCell(column);
        cell.setCellType(cellType);
        cell.setCellStyle(cellStyle);
        return cell;
    }
    
    
}
