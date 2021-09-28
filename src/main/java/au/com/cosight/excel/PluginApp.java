package au.com.cosight.excel;

import au.com.cosight.common.dto.plugin.CosightExecutionContext;
import au.com.cosight.sdk.annotation.EnableCosightRuntimeContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
@EnableCosightRuntimeContext
public class PluginApp implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger("excel-plugin");

    @Autowired
    private CosightExecutionContext executionContext;

    public static void main(String[] args)  {
        logger.info("{}", UUID.randomUUID().toString());

    }

    @Override
    public void run(String... args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        logger.info("plugin starting.. {}",executionContext.isLoaded());


        logger.info("EXECUTION CONTEXT {}",mapper.writeValueAsString(executionContext));
        logger.info("RUNTIME INFO {}",executionContext.getRuntimeInfo());
        logger.info("executionContext.isBatchProcess() => {}",executionContext.isBatchProcess());

    }
}
