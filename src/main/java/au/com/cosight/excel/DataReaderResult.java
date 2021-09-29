package au.com.cosight.excel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReaderResult {
    private int next;
    private List<Map<String,Object>> items;
    private Map<String,Object> metaData = new HashMap<>();

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }
}
