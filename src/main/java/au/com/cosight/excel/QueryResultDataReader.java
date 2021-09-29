package au.com.cosight.excel;

import au.com.cosight.entity.domain.QueryExecutionRequest;
import au.com.cosight.entity.domain.QueryExecutionResponse;
import au.com.cosight.sdk.query.QueryClient;

public class QueryResultDataReader implements DataReader {

    private final QueryClient client;
    private String vertex;

    public QueryResultDataReader(QueryClient client) {
        this.client = client;
    }

    @Override
    public DataReaderResult read(int startRow, int endRow) {
        DataReaderResult r = new DataReaderResult();
        QueryExecutionRequest request = new QueryExecutionRequest();
        request.setStartRow(startRow);
        request.setEndRow(endRow);
        QueryExecutionResponse response = client.execute(request);
        r.getMetaData().put("columns",response.getColumns());
        vertex = response.getName();
        r.setItems(response.getData().getData());
        if (r.getItems().size() > (endRow - startRow)){
            r.setNext(endRow + 1);
        }
        return null;
    }

    @Override
    public String getSheetName() {
        return vertex;
    }
}
