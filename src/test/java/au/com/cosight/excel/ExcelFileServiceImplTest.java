package au.com.cosight.excel;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class ExcelFileServiceImplTest {

    @Autowired
    private ExcelFileService excelFileService;

    @Test
    void createFileExists() throws Exception {
        InputStream inputStream = new FileInputStream("./src/test/resources/F01.xlsx");
        ExcelFile excelFile = excelFileService.create(inputStream,"./src/test/resource/F01.xlsx");
        assertTrue(excelFile.isLoaded());

    }
}
