package au.com.cosight.excel.integration;

import au.com.cosight.excel.ExcelFileService;
import au.com.cosight.sdk.annotation.EnableCosightDrive;
import au.com.cosight.sdk.query.QueryClient;
import au.com.cosight.sdk.query.QueryClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableCosightDrive
public class QueryExecuteTest {


    @Autowired
    private ExcelFileService excelFileService;

    @Test
    void executeQueryExcelSucces() throws Exception {
        QueryClient client = QueryClientBuilder.standard().build();

    }
}
