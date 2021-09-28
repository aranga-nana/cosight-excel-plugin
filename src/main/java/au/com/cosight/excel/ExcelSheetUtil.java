package au.com.cosight.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ExcelSheetUtil {

    private ExcelSheetUtil(){}

    public static Row createRow(Sheet sheet, int rowIndex){
        Row headerRow = sheet.getRow(rowIndex);
        if (headerRow == null) {
            headerRow = sheet.createRow(rowIndex);
        }
        return headerRow;
    }

    public static Cell createCell(Row row, CellType type, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            cell = row.createCell(index, type);
        }
        return cell;
    }
    public static void createHeaderRow(Row header, List<String> headerColumn){
        int colIndex = 0;
        for(String col: headerColumn) {
            Cell cell = header.getCell(colIndex);
            if (cell == null) {
                cell = header.createCell(colIndex, CellType.STRING);
            }
            cell.setCellValue(col);
            colIndex++;
        }
    }

    public static Workbook createWorkbook(InputStream stream, String ext) throws IOException {
        Workbook workbook = null;
        if (stream == null) {
            // need to create new excel file. existing file
            if (ext.equalsIgnoreCase("xlsx")) {
                workbook = new XSSFWorkbook();
            } else if (ext.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook();
            }
        } else {
            if (ext.equalsIgnoreCase("xlsx")) {
                workbook = new XSSFWorkbook(stream);
            } else if (ext.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook(stream);
            }
        }
        return workbook;
    }

    public static Sheet getSheetByName(String sheetName,Workbook workbook) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        return sheet;
    }
}
