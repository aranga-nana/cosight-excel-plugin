package au.com.cosight.excel.service;

import au.com.cosight.common.dto.plugin.CosightFile;
import au.com.cosight.excel.MainProcessor;
import au.com.cosight.sdk.annotation.EnableCosightDrive;
import au.com.cosight.sdk.annotation.EnableCosightRuntimeContext;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableCosightRuntimeContext
@EnableCosightDrive
class MainProcessorTest {

    @Autowired
    private ExcelFileService excelFileService;


    @Autowired
    private MainProcessor computeService;



    @Test
    void updateWorkbook() throws Exception{
       CosightFile cosightFile = new CosightFile();
       cosightFile.setLocalPath("test0.xlsx");

       Workbook workbook = excelFileService.create(cosightFile);

       boolean success = computeService.updateWorkbook(workbook);

       workbook.write(new FileOutputStream("test01.xlsx"));
       Assert.assertTrue(success);





    }
}
