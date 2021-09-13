package au.com.cosight.excel;

import java.io.IOException;
import java.io.InputStream;

public interface ExcelFileService {
    ExcelFile create(InputStream stream, String fileName) throws IOException;
}
