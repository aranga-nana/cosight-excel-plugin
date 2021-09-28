package au.com.cosight.excel;




public interface DataReader {

    DataReaderResult read(int start,int pageSize);
    String getSheetName();
}
