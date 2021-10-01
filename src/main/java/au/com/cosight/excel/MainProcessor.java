package au.com.cosight.excel;

import au.com.cosight.common.dto.plugin.CosightExecutionContext;
import au.com.cosight.common.dto.plugin.CosightFile;
import au.com.cosight.common.dto.plugin.PluginParameterUtils;
import au.com.cosight.common.dto.plugin.PluginParameterValue;
import au.com.cosight.entity.domain.QueryMetaData;
import au.com.cosight.entity.service.dto.ExpandedEntitiesDTO;
import au.com.cosight.excel.service.ExcelFileService;
import au.com.cosight.sdk.entities.instances.EntityInstanceClient;
import au.com.cosight.sdk.entities.instances.EntityInstanceClientBuilder;
import au.com.cosight.sdk.entity.EntityClient;
import au.com.cosight.sdk.entity.EntityClientBuilder;
import au.com.cosight.sdk.plugin.drive.CosightDriveManager;
import au.com.cosight.sdk.query.QueryClient;
import au.com.cosight.sdk.query.QueryClientBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

@Component
public class MainProcessor {

    private static Logger logger = LoggerFactory.getLogger(MainProcessor.class);


    private final CosightExecutionContext executionContext;
    private final ExcelFileService excelFileService;
    private final CosightDriveManager driveManager;



    public MainProcessor(CosightExecutionContext executionContext, ExcelFileService excelFileService, CosightDriveManager driveManager) {
        this.executionContext = executionContext;
        this.excelFileService = excelFileService;
        this.driveManager = driveManager;
    }
    public boolean updateWorkbook() throws Exception {
        final PluginParameterValue driveLocation = PluginParameterUtils.getValue(executionContext.getParameters(),"driveLocation");
        final PluginParameterValue prefix = PluginParameterUtils.getValue(executionContext.getParameters(),"prefix");
        if (driveLocation.isEmpty()) {
            logger.error("Driver location is empty");
            return false;
        }
        CosightFile  cosightFile = new CosightFile();
        cosightFile.setS3Key(driveLocation.asString());
        Workbook workbook = excelFileService.create(cosightFile);
        if (workbook == null) {
            return false;
        }
        boolean success = updateWorkbook(workbook);
        if (success) {
            String[] parts = driveLocation.asString().split("/");
            cosightFile.setLocalPath("/tmp/"+parts[parts.length -1]);
            File file = new File(cosightFile.getLocalPath());
            try (OutputStream outputStream = new FileOutputStream(file)){
                workbook.write(outputStream);
                return driveManager.driveInstance().copyLocal(file,generate3Key(cosightFile,prefix));
            }catch (Throwable e) {
                logger.error("{}",e.getMessage());
            }

        }
        return false;
    }
    private String generate3Key(CosightFile cosightFile,PluginParameterValue prefix) {
        String ext = FilenameUtils.getExtension(cosightFile.getLocalPath());
        String s3Key = cosightFile.getS3Key();
        if (prefix.isEmpty()) {
            return s3Key;
        }
        s3Key = s3Key.replaceAll("\\."+ext,"");
        s3Key += "-"+prefix.asString()+"."+ext;
        return s3Key;
    }
    public boolean updateWorkbook(Workbook workbook) throws Exception {


        PluginParameterValue entities = PluginParameterUtils.getValue(executionContext.getParameters(),"entities");
        PluginParameterValue queries = PluginParameterUtils.getValue(executionContext.getParameters(),"queries");
        if (entities.isEmpty() && queries.isEmpty()) {
            logger.error("NO Entities or Queries");
            return false;
        }
        if (!entities.isEmpty()) {
            processEntityInstance(workbook,entities.asStringList());
        }
        if (!queries.isEmpty()) {
            processQueries(workbook,queries.asStringList());
        }
        return true;
    }
    private void processQueries(Workbook workbook,List<String> queryUuids) throws Exception{
        QueryClient queryClient = QueryClientBuilder.standard().build();

        for(String uuid : queryUuids) {
            QueryMetaData metaData = queryClient.getMetadataByUuid(uuid);
            excelFileService.apply(new QueryResultDataReader(metaData.getClassName(),metaData.getUuid(),queryClient),metaData.getQueryFields(),workbook);
        }
    }
    private void processEntityInstance(Workbook workbook,List<String> entities) throws Exception{
        EntityClient entityClient = EntityClientBuilder.standard().build();
        EntityInstanceClient entityInstanceClient = EntityInstanceClientBuilder.standard().build();

        for(String entity : entities){
            final ExpandedEntitiesDTO expandedEntitiesDTO = entityClient.getExpandedEntityByClassName(entity);
            final DataReader reader = new EntityInstanceDataReader(entity,entityInstanceClient);
            excelFileService.apply(reader,expandedEntitiesDTO.getFields(),workbook);
        }
    }
}
