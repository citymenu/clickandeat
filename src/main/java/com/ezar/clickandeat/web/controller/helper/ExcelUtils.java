package com.ezar.clickandeat.web.controller.helper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

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
        if( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue() + "";
        }
        else {
            return cell.getStringCellValue();
        }
    }


    /**
     * @param workbook
     * @param cellName
     * @return
     */

    public static double getNamedCellDoubleValue(XSSFWorkbook workbook, String cellName) {
        XSSFCell cell = getNamedCell(workbook, cellName);
        if( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue();
        }
        else {
            String cellValue = cell.getStringCellValue();
            return StringUtils.hasText(cellValue)? Double.valueOf(cellValue.replace(",",".")): 0d;
        }
    }


    /**
     * @param sheet
     * @param rowIndex
     * @param colIndex
     * @return
     */

    public static String getCellStringValue(XSSFSheet sheet, int rowIndex, int colIndex ) {
        XSSFRow row = getRow(sheet, rowIndex);
        XSSFCell cell = getCell(row, colIndex);
        if( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue() + "";
        }
        else {
            return cell.getStringCellValue();
        }
    }


    /**
     * @param sheet
     * @param rowIndex
     * @param colIndex
     * @return
     */

    public static double getCellDoubleValue(XSSFSheet sheet, int rowIndex, int colIndex ) {
        XSSFRow row = getRow(sheet, rowIndex);
        XSSFCell cell = getCell(row, colIndex);
        if( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue();
        }
        else {
            String cellValue = cell.getStringCellValue();
            return StringUtils.hasText(cellValue)? Double.valueOf(cellValue.replace(",",".")): 0d;
        }
    }


    /**
     * @param workbook
     * @param areaName
     * @return
     */

    public static XSSFCell getFirstCell(XSSFWorkbook workbook, String areaName) {
        AreaReference areaReference = new AreaReference(workbook.getName(areaName).getRefersToFormula());
        CellReference anchorCellReference = areaReference.getFirstCell();
        XSSFSheet sheet = workbook.getSheet(anchorCellReference.getSheetName());
        XSSFRow row = sheet.getRow(anchorCellReference.getRow());
        return row.getCell(anchorCellReference.getCol());
    }


    /**
     * @param sheet
     * @param rowIndex
     * @return
     */

    public static XSSFRow getRow(XSSFSheet sheet, int rowIndex ) {
        return sheet.getRow(rowIndex) == null? sheet.createRow(rowIndex): sheet.getRow(rowIndex);
    }


    /**
     * @param row
     * @param colIndex
     * @return
     */

    public static XSSFCell getCell(XSSFRow row, int colIndex ) {
        return row.getCell(colIndex) == null? row.createCell(colIndex): row.getCell(colIndex);
    }


    /**
     * @param row
     * @param column
     * @param cellType
     * @return
     */

    public static XSSFCell createCell(XSSFRow row, int column, int cellType, CellStyle cellStyle ) {
        XSSFCell cell = row.getCell(column) == null? row.createCell(column): row.getCell(column);
        cell.setCellType(cellType);
        cell.setCellStyle(cellStyle);
        return cell;
    }

}
