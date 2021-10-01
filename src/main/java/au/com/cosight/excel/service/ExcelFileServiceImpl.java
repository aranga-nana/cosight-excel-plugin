package au.com.cosight.excel.service;

import au.com.cosight.common.dto.plugin.CosightFile;
import au.com.cosight.entity.service.dto.DataFieldsDTO;
import au.com.cosight.excel.DataReader;
import au.com.cosight.excel.DataReaderResult;
import au.com.cosight.excel.ExcelSheetUtil;
import au.com.cosight.sdk.plugin.drive.CosightDrive;
import au.com.cosight.sdk.plugin.drive.CosightDriveManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintrix.common.util.DateUtil;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;



@Service
public class ExcelFileServiceImpl implements ExcelFileService  {


    private final CosightDriveManager driveManager;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ExcelFileServiceImpl(CosightDriveManager driveManager) {
        this.driveManager = driveManager;
    }


    public Workbook create(CosightFile file) throws IOException {

        CosightDrive drive = driveManager.driveInstance();


        if (StringUtils.isEmpty(file.getS3Key())) {

            File f = new File(file.getLocalPath());
            if (f.exists()) {
                try (InputStream  stream = new FileInputStream(file.getLocalPath())) {
                    return ExcelSheetUtil.createWorkbook(stream, FilenameUtils.getExtension(f.getName()));
                }
            }
            return ExcelSheetUtil.createWorkbook(null,FilenameUtils.getExtension(f.getName()));
        }
        try (InputStream inputStream = drive.asInputStream(file.getS3Key())) {
            return ExcelSheetUtil.createWorkbook(inputStream,FilenameUtils.getExtension(file.getS3Key()));

        }catch (Throwable e){

        }
        return ExcelSheetUtil.createWorkbook(null,FilenameUtils.getExtension(file.getS3Key()));
    }

    public void apply(DataReader reader, List<DataFieldsDTO> columns, Workbook workbook) {

        Map<String,DataFieldsDTO>  dataFieldMap =
                columns.stream().collect(Collectors.toMap(DataFieldsDTO::getName, Function.identity()));

        Sheet sheet = ExcelSheetUtil.getSheetByName(reader.getSheetName(),workbook);

        List<String> headerColumn = createHeaderRow(columns,sheet);

        createDetail(reader,sheet,workbook,headerColumn,dataFieldMap);
    }
    private List<String> createHeaderRow( List<DataFieldsDTO> columns,Sheet sheet) {
        Row headerRow = ExcelSheetUtil.createRow(sheet,0);
        List<String> headerColumn = new ArrayList<>();
        final List<String> current = columns.stream().map(DataFieldsDTO::getName).sorted().collect(Collectors.toList());
        if (headerRow.getCell(0) == null) {
            ExcelSheetUtil.createHeaderRow(headerRow,current);
            return current;
        }

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
        ExcelSheetUtil.createHeaderRow(headerRow,current);
        return headerColumn;
    }

    private void createDetail(DataReader reader,Sheet sheet,Workbook workbook,List<String> headerColumn,Map<String,DataFieldsDTO> dataFieldMap) {
        int pageSize = 10000;
        int start = 0;
        int rowIndex =  1;
        while (true){
            int e =  start+pageSize;
            DataReaderResult result = reader.read(start,e);
            start = result.getNext();
            for(Map<String,Object> itemRow : result.getItems()) {
                Row excelRow =  ExcelSheetUtil.createRow(sheet,rowIndex);
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
        Cell cell = createCell(row,index,dto.getType());
        if (o == null) {
            cell.setCellValue("");
        }else if ("Decimal".equals(dto.getType())) {
            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###########0"));
            o = fixValue(o);
            if (o == null){
                o = "0";
            }
            cell.setCellValue(Long.valueOf(o.toString()));
        }else if ("Number".equals(dto.getType())) {
            o = fixValue(o);
            if (o == null){
                o = "0";
            }
            cell.setCellValue(Double.valueOf(o.toString()));
        }else if ("Date".equals(dto.getType()) || "DateTime".equals(dto.getType())) {
            o = fixValue(o);
            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            cell.setCellStyle(cellStyle);
            cell.setCellValue(DateUtil.convert(o.toString()));
        } else {
            o = fixValue(o);
            if (o == null){
                o = "";
            }
            cell.setCellValue(o.toString());
        }

    }
    private Object fixValue(Object o){
        if (o instanceof List) {
            o = ((List)o).get(0);
            return o;
        }
        return o;
    }
    private Cell createCell(Row row,int index,String type) {
        CellType cellType = CellType.STRING;
        if ("Decimal".equals(type) || "Number".equals(type)) {
            cellType = CellType.NUMERIC;
        }
        return ExcelSheetUtil.createCell(row,cellType,index);
    }



}
