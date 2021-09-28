package au.com.cosight.excel;

import au.com.cosight.common.dto.plugin.CosightFile;
import au.com.cosight.entity.service.dto.DataFieldsDTO;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.List;

public interface ExcelFileService {
    Workbook create(CosightFile file) throws IOException;
    void apply(DataReader reader,List<DataFieldsDTO> columns,Workbook workbook);

}
