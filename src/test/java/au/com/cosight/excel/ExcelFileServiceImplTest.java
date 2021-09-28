package au.com.cosight.excel;

import au.com.cosight.common.dto.plugin.CosightFile;
import au.com.cosight.entity.service.dto.DataFieldsDTO;
import au.com.cosight.sdk.annotation.EnableCosightDrive;
import com.fintrix.common.util.DateUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableCosightDrive
class ExcelFileServiceImplTest {

    @Autowired
    private ExcelFileService excelFileService;

    @Test
    void createFileExists() throws Exception {

        CosightFile file = new CosightFile();
        file.setLocalPath("./src/test/resources/F01.xlsx");
        Workbook workbook = excelFileService.create(file);
        assertNotNull(workbook);

    }

    @Test
    void createFileNot() throws Exception {
        CosightFile file = new CosightFile();
        file.setLocalPath("./src/test/resources/F02.xlsx");
        Workbook workbook = excelFileService.create(file);
        assertNotNull(workbook);
    }

    @Test
    void applyEmptySuccess() throws Exception{
        DataReader dataReader = new DataReader() {
            @Override
            public DataReaderResult read(int start, int pageSize) {
                DataReaderResult result = new DataReaderResult();
                result.setNext(0);
                List<Map<String,Object>> mockResult = new ArrayList<>();
                mockResult.add(ImmutableMap.of("Name","Paul","Age",20,"Dob","05/10/2021"));
                mockResult.add(ImmutableMap.of("Name","David","Age",45,"Dob","05/12/2020"));
                mockResult.add(ImmutableMap.of("Name","Rob","Age",45,"Dob","05/12/2020"));
                result.setItems(mockResult);
                return result;
            }

            @Override
            public String getSheetName() {
                return "Sheet1";
            }
        };
        List<DataFieldsDTO> dataFields = new ArrayList<>();
        DataFieldsDTO f = new DataFieldsDTO();
        f.setType("String");
        f.setName("Name");
        dataFields.add(f);

        f = new DataFieldsDTO();
        f.setType("Decimal");
        f.setName("Age");
        dataFields.add(f);

        f = new DataFieldsDTO();
        f.setType("Date");
        f.setName("Dob");
        dataFields.add(f);


        CosightFile file = new CosightFile();
        file.setLocalPath("./src/test/resources/F02.xlsx");
        Workbook workbook = excelFileService.create(file);

        excelFileService.apply(dataReader,dataFields,workbook);


        Assert.assertTrue(workbook.getNumberOfSheets() == 1);
        Assert.assertEquals("20.0",workbook.getSheetAt(0).getRow(1).getCell(0).getNumericCellValue()+"");
        Assert.assertEquals(DateUtil.convert("05/10/2021"),workbook.getSheetAt(0).getRow(1).getCell(1).getDateCellValue());
        Assert.assertEquals("Paul",workbook.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());

    }
    @Test
    void applyExistingSheetSuccess() throws Exception{
        DataReader dataReader = new DataReader() {
            @Override
            public DataReaderResult read(int start, int pageSize) {
                DataReaderResult result = new DataReaderResult();
                result.setNext(0);
                List<Map<String,Object>> mockResult = new ArrayList<>();
                mockResult.add(ImmutableMap.of("Name","Paul","Age",20,"Dob","05/10/2021"));
                mockResult.add(ImmutableMap.of("Name","David","Age",45,"Dob","05/12/2020"));
                mockResult.add(ImmutableMap.of("Name","Rob","Age",45,"Dob","05/12/2020"));
                mockResult.add(ImmutableMap.of("Name","L1","Age",45,"Dob","05/11/2020"));
                mockResult.add(ImmutableMap.of("Name","L2","Age",30,"Dob","06/07/1980"));
                result.setItems(mockResult);
                return result;
            }

            @Override
            public String getSheetName() {
                return "Employee";
            }
        };
        List<DataFieldsDTO> dataFields = new ArrayList<>();
        DataFieldsDTO f = new DataFieldsDTO();
        f.setType("String");
        f.setName("Name");
        dataFields.add(f);

        f = new DataFieldsDTO();
        f.setType("Decimal");
        f.setName("Age");
        dataFields.add(f);

        f = new DataFieldsDTO();
        f.setType("Date");
        f.setName("Dob");
        dataFields.add(f);


        CosightFile file = new CosightFile();
        file.setLocalPath("./src/test/resources/Exist01.xlsx");
        Workbook workbook = excelFileService.create(file);

        excelFileService.apply(dataReader,dataFields,workbook);


        Assert.assertTrue(workbook.getNumberOfSheets() == 1);
        // check the last row
        workbook.write(new FileOutputStream("TT1.xlsx"));
        Assert.assertEquals("30.0",workbook.getSheetAt(0).getRow(5).getCell(0).getNumericCellValue()+"");
        Assert.assertEquals(DateUtil.convert("06/07/1980"),workbook.getSheetAt(0).getRow(5).getCell(1).getDateCellValue());
        Assert.assertEquals("L2",workbook.getSheetAt(0).getRow(5).getCell(2).getStringCellValue());

    }
    @Test
    void applyExistingSheetAdditonalColumnSuccess() throws Exception{
        DataReader dataReader = new DataReader() {
            @Override
            public DataReaderResult read(int start, int pageSize) {
                DataReaderResult result = new DataReaderResult();
                result.setNext(0);
                List<Map<String,Object>> mockResult = new ArrayList<>();
                mockResult.add(ImmutableMap.of("Name","Paul","BCODE","12142","Age",20,"Dob","05/10/2021"));
                mockResult.add(ImmutableMap.of("Name","David","BCODE","141222","Age",45,"Dob","05/12/2020"));
                mockResult.add(ImmutableMap.of("Name","Rob","BCODE","126222","Age",45,"Dob","05/12/2020"));
                mockResult.add(ImmutableMap.of("Name","L1","BCODE","121722","Age",45,"Dob","05/11/2020"));
                mockResult.add(ImmutableMap.of("Name","L2","BCODE","129022","Age",30,"Dob","06/07/1980"));
                result.setItems(mockResult);
                return result;
            }

            @Override
            public String getSheetName() {
                return "Employee";
            }
        };
        List<DataFieldsDTO> dataFields = new ArrayList<>();
        DataFieldsDTO f = new DataFieldsDTO();
        f.setType("String");
        f.setName("Name");
        dataFields.add(f);

        f = new DataFieldsDTO();
        f.setType("String");
        f.setName("BCODE");
        dataFields.add(f);

        f = new DataFieldsDTO();
        f.setType("Decimal");
        f.setName("Age");
        dataFields.add(f);

        f = new DataFieldsDTO();
        f.setType("Date");
        f.setName("Dob");
        dataFields.add(f);


        CosightFile file = new CosightFile();
        file.setLocalPath("./src/test/resources/Exist01.xlsx");
        Workbook workbook = excelFileService.create(file);

        excelFileService.apply(dataReader,dataFields,workbook);


        Assert.assertTrue(workbook.getNumberOfSheets() == 1);
        // check the last row

        Assert.assertEquals("30.0",workbook.getSheetAt(0).getRow(5).getCell(0).getNumericCellValue()+"");
        Assert.assertEquals(DateUtil.convert("06/07/1980"),workbook.getSheetAt(0).getRow(5).getCell(1).getDateCellValue());
        Assert.assertEquals("L2",workbook.getSheetAt(0).getRow(5).getCell(2).getStringCellValue());
        Assert.assertEquals("129022",workbook.getSheetAt(0).getRow(5).getCell(3).getStringCellValue());

    }
}
