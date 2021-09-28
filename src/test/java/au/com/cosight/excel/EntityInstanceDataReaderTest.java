package au.com.cosight.excel;

import au.com.cosight.sdk.entities.instances.EntityInstanceClient;
import au.com.cosight.sdk.entities.instances.EntityInstanceClientBuilder;
import au.com.cosight.sdk.enums.CosightEndpoint;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class EntityInstanceDataReaderTest {


    @Test
    void readSuccess() throws Exception {
        EntityInstanceClient client = EntityInstanceClientBuilder.standard().build();
        EntityInstanceDataReader reader = new EntityInstanceDataReader("Salary", client);

        DataReaderResult result = reader.read(0, 4);
        Assert.assertTrue(result.getItems().size() > 0);
    }

}
