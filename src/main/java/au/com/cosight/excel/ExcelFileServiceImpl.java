package au.com.cosight.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelFileServiceImpl implements ExcelFileService  {

    public ExcelFile create(InputStream stream,String fileName) throws IOException {
        ExcelFile excelFile = new ExcelFile();
        String[] parts = fileName.split("\\.");
        String ext = parts[parts.length -1];
        Workbook workbook = null;
        if (stream == null) {
            // need to create new excel file. existing file
            if (ext.equalsIgnoreCase("xlsx")) {
                workbook = new XSSFWorkbook();
            } else if (ext.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook();
            }
        }else {
            if (ext.equalsIgnoreCase("xlsx")) {
                workbook = new XSSFWorkbook(stream);
            } else if (ext.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook(stream);
            }
        }
        excelFile.setWorkBook(workbook);

        return  excelFile;
    }
}
