package au.com.cosight.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


import au.com.cosight.common.dto.plugin.CosightFile;
import au.com.cosight.entity.service.dto.DataFieldsDTO;
import au.com.cosight.sdk.plugin.drive.CosightDrive;
import au.com.cosight.sdk.plugin.drive.CosightDriveManager;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.fintrix.common.util.DateUtil;


@Service
public class ExcelFileServiceImpl implements ExcelFileService  {


    private final CosightDriveManager driveManager;

    public ExcelFileServiceImpl(CosightDriveManager driveManager) {
        this.driveManager = driveManager;
    }


    public Workbook create(CosightFile file) throws IOException {
        CosightDrive drive = driveManager.driveInstance();
        InputStream stream = null;
        String fileName = file.getS3Key();
        if (StringUtils.isEmpty(file.getS3Key())) {
            fileName = file.getLocalPath();
            File f = new File(file.getLocalPath());
            if (f.exists()) {
               stream = new FileInputStream(file.getLocalPath());
            }
        }else {
           stream =  drive.asInputStream(file.getS3Key());
        }

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
        return  workbook;
    }



    public void apply(DataReader reader,List<DataFieldsDTO> columns,Workbook workbook) throws Exception{

        Map<String,DataFieldsDTO>  dataFieldMap =
                columns.stream().collect(Collectors.toMap(DataFieldsDTO::getName, Function.identity()));

        Sheet sheet = workbook.getSheet(reader.getSheetName());
        final List<String> headerColumn = new ArrayList<>();
        if (sheet == null) {
            int colIndex = 0;
            sheet = workbook.createSheet(reader.getSheetName());
            headerColumn.addAll(columns.stream().map(DataFieldsDTO::getName).sorted().collect(Collectors.toList()));
            createHeader(sheet,headerColumn);
        }else {
            sheet = workbook.getSheet(reader.getSheetName());
            if (sheet == null) {
                sheet = workbook.createSheet(reader.getSheetName());
            }
            Row headerRow = sheet.getRow(0);
            final List<String> current = columns.stream().map(DataFieldsDTO::getName).sorted().collect(Collectors.toList());
            if (headerRow == null) {
                headerColumn.addAll(current);
            }else {
                int colIndex = 0;
                while (true) {
                    final  Cell cell = headerRow.getCell(colIndex);
                    if (cell == null) {
                        break;
                    }
                    headerColumn.add(cell.getStringCellValue());
                    colIndex++;
                }
                if (!ListUtils.isEqualList(current,headerColumn)) {
                    List<String> addtional = current.stream().filter(s -> !headerColumn.contains(s)).collect(Collectors.toList());
                    headerColumn.addAll(addtional);
                }
                createHeader(sheet,headerColumn);
            }
        }

        int pageSize = 10000;
        int start = 0;
        int rowIndex =  1;
        while (true){
            int e =  start+pageSize;
            DataReaderResult result = reader.read(start,e);
            start = result.getNext();
            for(Map<String,Object> itemRow : result.getItems()) {
                Row excelRow = null;
                excelRow =  sheet.getRow(rowIndex);
                if (excelRow == null) {
                    excelRow =  sheet.createRow(rowIndex);
                }
                int colIndex = 0;
                for(String col: headerColumn){
                    DataFieldsDTO dto = dataFieldMap.get(col);
                    if (dto != null) {
                        writeCellValue(colIndex,itemRow.get(col),dto,excelRow,workbook);
                    }
                    colIndex++;
                }
                rowIndex++;
            }

            if (result.getNext() == 0) {
                break;
            }
        }

    }

    private void writeCellValue(int index,Object o,DataFieldsDTO dto,Row row,Workbook workbook){

        Cell cell = row.getCell(index);
        if (o == null) {
            if (cell == null) {
                cell = row.createCell(index, CellType.STRING);
            }
            cell.setCellValue("");
        }else if ("Decimal".equals(dto.getType())) {
            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###########0"));
            if (cell == null) {
                cell = row.createCell(index,CellType.NUMERIC);
            }

            cell.setCellValue(Long.valueOf(o.toString()));
        }else if ("Number".equals(dto.getType())) {
            if (cell == null) {
                cell = row.createCell(index,CellType.NUMERIC);
            }
            cell.setCellValue(Double.valueOf(o.toString()));
        }else if ("Date".equals(dto.getType()) || "DateTime".equals(dto.getType())) {
            if (cell == null) {
                cell = row.createCell(index, CellType.STRING);
            }
            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(DateUtil.convert(o.toString()));
        } else {
            if (cell == null) {
                cell = row.createCell(index, CellType.STRING);
            }
            cell.setCellValue(o.toString());
        }

    }

    private void createHeader(Sheet sheet, List<String> headerColumn){
        Row header = sheet.createRow(0);
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

}
