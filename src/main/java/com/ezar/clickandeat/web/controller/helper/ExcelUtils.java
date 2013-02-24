package com.ezar.clickandeat.web.controller.helper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    /**
     * @param workbook
     * @param rangeName
     * @param value
     */

    public static void writeToNamedCell(XSSFWorkbook workbook, String rangeName, String value) {
        if( value != null ) {
            getNamedCell(workbook, rangeName).setCellValue(value);
        }
    }


    /**
     * @param workbook
     * @param rangeName
     * @param value
     */

    public static void writeToNamedCell(XSSFWorkbook workbook, String rangeName, Double value) {
        if( value != null ) {
            getNamedCell(workbook, rangeName).setCellValue(value);
        }
    }


    /**
     * @param workbook
     * @param rangeName
     * @param value
     */

    public static void writeToNamedCell(XSSFWorkbook workbook, String rangeName, Integer value) {
        if( value != null ) {
            getNamedCell(workbook, rangeName).setCellValue(value);
        }
    }

    /**
     * @param workbook
     * @param rangeName
     * @return
     */

    public static XSSFCell getNamedCell(XSSFWorkbook workbook, String rangeName) {
        CellReference cellReference = new CellReference(workbook.getName(rangeName).getRefersToFormula());
        XSSFSheet sheet = workbook.getSheet(cellReference.getSheetName());
        XSSFRow row = sheet.getRow(cellReference.getRow());
        return row.getCell(cellReference.getCol());
    }

    
    /**
     * @param workbook
     * @param cellName
     * @return
     */

    public static String getNamedCellStringValue(XSSFWorkbook workbook, String cellName) {
        XSSFCell cell = getNamedCell(workbook, cellName);
        return cell.getStringCellValue();
    }


    /**
     * @param workbook
     * @param cellName
     * @return
     */

    public static double getNamedCellDoubleValue(XSSFWorkbook workbook, String cellName) {
        XSSFCell cell = getNamedCell(workbook, cellName);
        return cell.getNumericCellValue();
    }

}
