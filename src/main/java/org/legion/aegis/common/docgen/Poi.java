package org.legion.aegis.common.docgen;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.legion.aegis.common.utils.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author yuzhou
 * xlsx opreation class
 */
public final class Poi {

    public static XSSFWorkbook getXlsxWorkbookByFile(String path) {
        XSSFWorkbook workbook = null;
        String suffix = FileNameUtils.getExtension(path);
        if (!"xlsx".equalsIgnoreCase(suffix)) {
            return null;
        }
        File file = new File(path);
        if (file.exists()) {
            try {
                FileInputStream stream = new FileInputStream(path);
                workbook = new XSSFWorkbook(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return workbook;
    }

    public static XSSFCellStyle stdContentCellStyle(XSSFWorkbook workbook) {
        if (workbook == null) {
            return null;
        }
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("微软雅黑");
        cellStyle.setFont(font);
        return cellStyle;
    }

    public static String getSheetName(XSSFWorkbook workbook,int index) {
        return workbook != null && index >= 0 ? workbook.getSheetName(index) : null;
    }

    public static XSSFSheet getSheet(XSSFWorkbook workbook,String sheetName) {
        return (workbook != null && !StringUtils.isEmpty(sheetName)) ? workbook.getSheet(sheetName) : null;
    }

    public static XSSFRow getRow(XSSFSheet sheet, int r) {
        return sheet.getRow(r);
    }

    public static XSSFCell getCell(XSSFRow row, int c) {
        return row.getCell(c);
    }

    public static XSSFRow createRow(XSSFSheet sheet, int r) {
        return sheet.createRow(r);
    }

    public static XSSFCell createCell(XSSFRow row, int c) {
        return row.createCell(c);
    }

    public static void autoSizeColumn(XSSFSheet sheet, int c) {
        sheet.autoSizeColumn(c);
    }

    public static void setValue(XSSFCell cell, String value, XSSFCellStyle style) {
        if (cell != null) {
            cell.setCellStyle(style);
            cell.setCellValue(value);
        }
    }

    /**
     * this method is used for write
     * get Cell from workbook, sheet and cell will be created if do not exsit
     * @param workbook xlsx workbook
     * @param sheetName xlsx doc sheet name
     * @param rowNum row number start from 0
     * @param cellNum column number start from 0
     * @return cell
     */
    @Deprecated
    public static XSSFCell getCell(XSSFWorkbook workbook, String sheetName, int rowNum, int cellNum) {
        if (workbook == null || rowNum < 0 || cellNum < 0 || StringUtils.isEmpty(sheetName)) {
            return null;
        }
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        XSSFRow row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        XSSFCell cell = row.getCell(cellNum);
        if (cell == null) {
            cell = row.createCell(cellNum);
        }
        return cell;
    }

    public static int getFirstRow(XSSFWorkbook workbook, String sheetName) {
        if (workbook != null && !sheetName.isEmpty()) {
            XSSFSheet sheet = getSheet(workbook, sheetName);
            return sheet.getFirstRowNum();
        }
        return -1;
    }

    public static int getLastRow(XSSFWorkbook workbook, String sheetName) {
        if (workbook != null && !sheetName.isEmpty()) {
            XSSFSheet sheet = getSheet(workbook, sheetName);
            return sheet.getLastRowNum();
        }
        return -1;
    }



    @Deprecated
    public static XSSFWorkbook writeCell(XSSFWorkbook workbook, String sheetName, int rowNum, int cellNum, String value, XSSFCellStyle style) {
        if (workbook != null) {
            XSSFCell cell = getCell(workbook, sheetName, rowNum, cellNum);
            XSSFSheet sheet = workbook.getSheet(sheetName);
            sheet.autoSizeColumn(cellNum);
            if (style != null) {
                cell.setCellStyle(style);
            }
            cell.setCellValue(value);
        }
        return workbook;
    }

    @Deprecated
    public static void writeRow(XSSFWorkbook workbook, String sheetName, int rowNum, int colStartNum, List<String> content, List<XSSFCellStyle> styles) {
        if (workbook == null || StringUtils.isEmpty(sheetName) || rowNum < 0 || content == null) {
            return;
        }
        if (styles != null) {
            int styleSize = styles.size();
            for (int i = 0; i < content.size(); i++) {
                if (i < styleSize) {
                    writeCell(workbook, sheetName, rowNum, colStartNum + i, content.get(i), styles.get(i));
                } else {
                    writeCell(workbook, sheetName, rowNum, colStartNum + i, content.get(i), null);
                }
            }
        } else {
            for (int i = 0; i < content.size(); i++) {
                writeCell(workbook, sheetName, rowNum, colStartNum + i, content.get(i), null);
            }
        }
    }

    public static String readCell(XSSFWorkbook workbook, String sheetName, int rw, int col) {
        if (workbook == null || StringUtils.isEmpty(sheetName) || rw < 0 || col < 0) {
            return null;
        }
        XSSFSheet sheet = workbook.getSheet(sheetName);
        String value = null;
        if (sheet != null) {
            XSSFRow row = sheet.getRow(rw);
            if (row != null) {
                XSSFCell cell = row.getCell(col);
                value = convertCellValue(cell);
            }
        }
        return value;
    }

    public static String readCell(XSSFCell cell) {
        return convertCellValue(cell);
    }

    public static List<String> readRow(XSSFSheet sheet, int rowNum) {
        List<String> rowContent = new ArrayList<String>();
        if (sheet != null) {
            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();
            if (firstRow <= rowNum && lastRow >= rowNum) {
                XSSFRow row = sheet.getRow(rowNum);
                int firstColumn = row.getFirstCellNum();
                int lastColumn = row.getLastCellNum();
                for (int j = firstColumn; j < lastColumn; j++) {
                    rowContent.add(convertCellValue(row.getCell(j)));
                }
            }
        }
        return rowContent;
    }

    public static List<String> readRowContent(XSSFRow row) {
        List<String> rowContent = new ArrayList<String>();
        if (row != null) {
            int firstColumn = row.getFirstCellNum();
            int lastColumn = row.getLastCellNum();
            for (int i = firstColumn; i < lastColumn; i++) {
                rowContent.add(convertCellValue(row.getCell(i)));
            }
        }
        return rowContent;
    }

    public static XSSFRow readXSSFRow(XSSFSheet sheet, int rowNum) {
        XSSFRow row = null;
        if (sheet != null) {
            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();
            if (firstRow <= rowNum && lastRow >= rowNum) {
                row = sheet.getRow(rowNum);
            }
        }
        return row;
    }

    public static Map<Integer, XSSFRow> readRowMap(XSSFSheet sheet) {
        Map<Integer, XSSFRow> rowMap = new HashMap<>();
        if (sheet != null) {
            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();
            for (int i = firstRow; i <= lastRow; i++) {
                XSSFRow row = readXSSFRow(sheet, i);
                rowMap.put(i, row);
            }
        }
        return rowMap;
    }

    public static List<List<String>> readAll(XSSFSheet sheet, int start, int end) {
        List<List<String>> allContent = null;
        if (sheet != null) {
            allContent = new ArrayList<List<String>>();
            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();
            if (end >= start && end <= lastRow) {
                for (int i = firstRow; i <= lastRow; i++) {
                    List<String> row = readRow(sheet, i + start);
                    allContent.add(row);
                    if (i + start == end && (start != 0 || end != 0))
                        break;
                }
            }
        }
        return allContent;
    }

    public static List<List<String>> readAll(XSSFSheet sheet) {
        return readAll(sheet, 0, 0);
    }

    public static void save(XSSFWorkbook workbook, String path) {
        if (workbook != null) {
            try {
                FileOutputStream stream = new FileOutputStream(path);
                workbook.write(stream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] saveToByteArray(XSSFWorkbook workbook) {
        byte[] data = null;
        if (workbook != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                workbook.write(stream);
                workbook.close();
                data = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private static String convertCellValue(XSSFCell cell) {
        String value = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case FORMULA :
                    value = cell.getCellFormula();
                    break;
                case STRING:
                    value = cell.getStringCellValue();
                    break;
                case BLANK:
                    value = "";
                    break;
                default:
                    value = null;
                    break;
            }
        }
        return value;
    }

    public static String getNumericCoordinate(String coordinate) {
        if (!StringUtils.isEmpty(coordinate)) {
            String[] arr = StringUtils.convertToArray(coordinate);
            StringBuilder alpha = new StringBuilder();
            StringBuilder number = new StringBuilder();
            int alphaSum = 0;
            for (String str : arr) {
                if (StringUtils.isLetter(str)) {
                    alpha.append(str.toUpperCase());
                } else {
                    number.append(str);
                }
            }
            for (int i = 0; i <alpha.length(); i++) {
                alphaSum += StringUtils.getLetterIndex(String.valueOf(alpha.charAt(i))) * Math.pow(26, alpha.length() - i - 1);
            }
            return alphaSum + "," + number.toString();
        }
        return null;
    }

}
