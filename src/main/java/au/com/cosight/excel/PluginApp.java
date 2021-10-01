package au.com.cosight.excel;

import au.com.cosight.common.dto.plugin.CosightExecutionContext;
import au.com.cosight.sdk.annotation.EnableCosightDrive;
import au.com.cosight.sdk.annotation.EnableCosightRuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCosightRuntimeContext
@EnableCosightDrive
public class PluginApp implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger("excel-plugin");

    private final CosightExecutionContext executionContext;
    private final MainProcessor process;

    public PluginApp(CosightExecutionContext executionContext, MainProcessor process) {
        this.executionContext = executionContext;
        this.process = process;
    }


    public static void main(String[] args)  {
        SpringApplication.run(PluginApp.class,args);

    }

    @Override
    public void run(String... args) throws Exception {
        if (executionContext == null) {
            return;
        }
        logger.info("plugin starting.. {}",executionContext.isLoaded());
        logger.info("RUNTIME INFO {}",executionContext.getRuntimeInfo());
        logger.info("executionContext.isBatchProcess() => {}",executionContext.isBatchProcess());
        try {
            process.updateWorkbook();
        }catch (Exception e) {
            logger.error("ERROR {}",e.getMessage());
        }
    }
}
