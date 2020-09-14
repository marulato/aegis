package org.legion.aegis.common.docgen;

import org.apache.poi.xssf.usermodel.*;
import org.legion.aegis.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Author: Yuzhou
 * Class: Excel
 * Version: master 3, branch none
 *
 */
public class Excel {

    private String path;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private Map<Integer, XSSFRow> rowMap;
    private String sheetName;
    private List<List<String>> content;
    private XSSFCellStyle stdStyle;
    private int minimumCol;
    private int maxCol;

    /**
     * After called, the content will be read into the memory if the file exists.
     * If not exists, an empty excel file will be created in the memory using the params provided
     * @param path the path of the existing excel file, or where the new file will be saved
     * @param sheetName the name of the sheet operated during the procedure
     */
    public Excel(String path, String sheetName) {
        this.path = path;
        this.sheetName = sheetName;
        workbook = Poi.getXlsxWorkbookByFile(path);
        rowMap = new HashMap<>();
        content = new ArrayList<>();
        if (workbook != null) {
            sheet = Poi.getSheet(workbook, sheetName);
            initData();
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(sheetName);
        }
        stdStyle = Poi.stdContentCellStyle(workbook);
    }

    private void initData() {
        rowMap = Poi.readRowMap(sheet);
        rowMap.forEach((rn, row) -> {
            List<String> content = Poi.readRowContent(row);
            this.content.add(content);
        });
    }

    /**
     * Get the content from the cell by coordinate
     * @param coordinate the coordiante of the cell in excel like 'A1'
     * @return the content of the cell as a string
     */
    public String getValue(String coordinate) {
        String cord = Poi.getNumericCoordinate(coordinate);
        if (StringUtils.isNotEmpty(cord)) {
            String[] matrix = cord.split(",");
            return getValue(Integer.parseInt(matrix[1]) - 1, Integer.parseInt(matrix[0]) - 1);
        }
        return null;
    }

    /**
     * Get the content from the cell by row number and column number
     * @param rn row number
     * @param cn column number
     * @return the content of the cell as a string
     */
    public String getValue(int rn, int cn) {
        return Poi.readCell(rowMap.get(rn) == null ? null : rowMap.get(rn).getCell(cn));
    }

    /**
     * Set the content for the cell by coordinate
     * @param coordinate the coordinate of the cell in excel like 'A1'
     * @param value content
     */
    public void setValue(String coordinate, String value) {
        String cord = Poi.getNumericCoordinate(coordinate);
        if (StringUtils.isNotEmpty(cord)) {
            String[] matrix = cord.split(",");
            setValue(Integer.parseInt(matrix[1]) - 1, Integer.parseInt(matrix[0]) - 1, value);
        }
    }

    /**
     *
     * @param rn row number
     * @param cn column number
     * @param value content
     */
    public void setValue(int rn, int cn, String value) {
        XSSFRow row = getRow(rn);
        if (row != null && StringUtils.isNotEmpty(value)) {
            XSSFCell cell = getCell(row, cn);
            Poi.setValue(cell, value, stdStyle);
        }
    }

    public void setRow(int rn, List<String> content, int start) {
        XSSFRow row = getRow(rn);
        if (row != null && content != null && !content.isEmpty()) {
            if (start < minimumCol) {
                minimumCol = start;
            }
            if (content.size() + start > maxCol) {
                maxCol = content.size() + start - 1;
            }
            for (int i = start; i < content.size() + start; i++) {
                XSSFCell cell = getCell(row, i);
                Poi.setValue(cell, content.get(i - start), stdStyle);
            }
        }
    }

    public void setRow(int rn, List<String> content) {
        setRow(rn, content, 0);
    }

    public void save() {
        int size = (maxCol + 1 - minimumCol) / 5;
        try {
            autoSizeColumn(size).await();
            Poi.save(workbook, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] toByteArray() {
        int size = (maxCol + 1 - minimumCol) / 5;
        try {
            autoSizeColumn(size).await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Poi.saveToByteArray(workbook);
    }

    private XSSFRow getRow(int rn) {
        XSSFRow row = null;
        if (rn >= 0) {
            row = rowMap.get(rn);
            if (row == null) {
                row = sheet.createRow(rn);
                rowMap.put(rn, row);
            }
        }
        return row;
    }

    private XSSFCell getCell(XSSFRow row, int cn) {
        XSSFCell cell = null;
        if (cn >= 0) {
            cell = row.getCell(cn);
            if (cell == null) {
                cell = row.createCell(cn);
            }
        }
        return cell;
    }

    private CountDownLatch autoSizeColumn(int size) {
        CountDownLatch latch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            new Thread(new RunAutoSize(i * 5, i * 5 + 4, latch)).start();
        }
        return latch;
    }

    private void autoSizeColumn(int from, int to) {
        for (int i = from; i <= to; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private class RunAutoSize implements Runnable {
        int start;
        int end;
        CountDownLatch latch;

        RunAutoSize(int start, int end, CountDownLatch latch) {
            this.start = start;
            this.end = end;
            this.latch = latch;
        }
        @Override
        public void run() {
            autoSizeColumn(start, end);
            latch.countDown();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public XSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(XSSFSheet sheet) {
        this.sheet = sheet;
    }

    public Map<Integer, XSSFRow> getRowMap() {
        return rowMap;
    }

    public void setRowMap(Map<Integer, XSSFRow> rowMap) {
        this.rowMap = rowMap;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<List<String>> getContent() {
        return content;
    }

    public void setContent(List<List<String>> content) {
        this.content = content;
    }

    public XSSFCellStyle getStdStyle() {
        return stdStyle;
    }

    public void setStdStyle(XSSFCellStyle stdStyle) {
        this.stdStyle = stdStyle;
    }

    public int getMinimumCol() {
        return minimumCol;
    }

    public void setMinimumCol(int minimumCol) {
        this.minimumCol = minimumCol;
    }

    public int getMaxCol() {
        return maxCol;
    }

    public void setMaxCol(int maxCol) {
        this.maxCol = maxCol;
    }
}
