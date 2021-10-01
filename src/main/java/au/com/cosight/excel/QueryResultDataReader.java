package au.com.cosight.excel;

import au.com.cosight.entity.domain.QueryExecutionRequest;
import au.com.cosight.entity.domain.QueryExecutionResponse;
import au.com.cosight.sdk.query.QueryClient;

public class QueryResultDataReader implements DataReader {

    private final QueryClient client;
    private String vertex;
    private String uuid;

    public QueryResultDataReader(String vertex,String uuid, QueryClient client) {
        this.client = client;
        this.vertex = vertex;
        this.uuid = uuid;
    }

    @Override
    public DataReaderResult read(int startRow, int endRow) {
        DataReaderResult r = new DataReaderResult();
        QueryExecutionRequest request = new QueryExecutionRequest();
        request.setStartRow(startRow);
        request.setEndRow(endRow);
        request.setUuid(uuid);
        QueryExecutionResponse response = client.execute(request);
        r.getMetaData().put("columns",response.getColumns());
        r.setItems(response.getData().getData());
        if (r.getItems().size() > (endRow - startRow)){
            r.setNext(endRow + 1);
        }
        return r;
    }

    @Override
    public String getSheetName() {
        return vertex;
    }
}
