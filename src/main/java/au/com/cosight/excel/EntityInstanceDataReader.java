package au.com.cosight.excel;

import au.com.cosight.sdk.entities.instances.EntityInstanceClient;


public class EntityInstanceDataReader implements DataReader {

    private String vertexName;
    private EntityInstanceClient client;
    public EntityInstanceDataReader(String vertexName, EntityInstanceClient client){
        this.vertexName = vertexName;
        this.client = client;

    }
    @Override
    public DataReaderResult read(int start, int endRow) {
        DataReaderResult r = new DataReaderResult();
        r.setItems(client.listInstances(vertexName,start,endRow));
        if (r.getItems().size() > (endRow - start)){
            r.setNext(endRow + 1);
        }
        return r;
    }

    @Override
    public String getSheetName() {
        return vertexName;
    }
}
