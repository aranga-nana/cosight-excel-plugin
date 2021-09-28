package au.com.cosight.excel;


import org.apache.poi.ss.usermodel.Workbook;

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
