package au.com.cosight.excel;


import au.com.cosight.entity.service.dto.DataFieldsDTO;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExcelFile {
    private Workbook workbook;
    private boolean loaded = false;


    public void setWorkBook(Workbook workBook) {
        if (workBook != null) {
            loaded = true;
        }
        this.workbook = workBook;
    }

    public Workbook asWorkbook() {
        return workbook;
    }

    public boolean isLoaded() {
        return loaded;
    }


}
