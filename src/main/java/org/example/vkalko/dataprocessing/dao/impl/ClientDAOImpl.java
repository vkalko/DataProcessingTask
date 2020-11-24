package org.example.vkalko.dataprocessing.dao.impl;

import com.google.cloud.bigquery.*;
import org.example.vkalko.dataprocessing.dao.ClientDAO;
import org.example.vkalko.dataprocessing.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ClientDAOImpl implements ClientDAO {

    private final BigQuery bigQuery;

    @Value("${spring.cloud.gcp.config.project-id}") private String projectId;

    @Value("${spring.cloud.gcp.bigquery.dataset-name}") private String datasetName;

    @Value("${spring.cloud.gcp.bigquery.table-name-req}") private String tableRequired;

    @Autowired
    public ClientDAOImpl() {
        bigQuery = BigQueryOptions.newBuilder().setProjectId(projectId).build().getService();
    }

    @Override
    public InsertAllResponse insert(Client client, String tableName) {
        if (client == null) return null;
        TableId tableId;
        InsertAllRequest.RowToInsert row;

        //inserting required fields
        if (tableName.equals(tableRequired)) {
            tableId = TableId.of(projectId, datasetName, tableRequired);
            row = mapToRowReq(clientToMap(client));

        //inserting all fields
        }  else {
            tableId = TableId.of(projectId, datasetName, tableName);
            row = mapToRowAll(clientToMap(client));
        }
        return bigQuery.insertAll(InsertAllRequest.newBuilder(tableId).
                addRow(row).
                build());
    }

    @Override
    public InsertAllResponse insertAll(Iterable<Client> clients, String tableName) {

        List<InsertAllRequest.RowToInsert> rowsToInsert = new ArrayList<>();
        TableId tableId;

        //inserting required fields
        if (tableName.equals(tableRequired)) {
            tableId = TableId.of(projectId, datasetName, tableRequired);
            for (Client client: clients) {
                rowsToInsert.add(mapToRowReq(clientToMap(client)));
            }

        //inserting all fields
        } else {
            tableId = TableId.of(projectId, datasetName, tableName);
            for (Client client: clients) {
                rowsToInsert.add(mapToRowAll(clientToMap(client)));
            }
        }
        synchronized (this) {
            return bigQuery.insertAll(InsertAllRequest.newBuilder(tableId).
                    setRows(rowsToInsert).
                    build());
        }
    }

    //converting Client objects to Map first to have unified way of adding objects to BQ
    private Map<String, Object> clientToMap(Client client) {
        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> address = new HashMap<>();
        address.put("street", client.getAddress().getStreet().toString());
        address.put("city", client.getAddress().getCity().toString());
        address.put("zip", client.getAddress().getZip().toString());

        fields.put("id", client.getId());
        fields.put("name", client.getName().toString());
        fields.put("phone", client.getPhone().toString());
        fields.put("address", address);
        fields.put("month_payment", client.getMonthPayment());
        return fields;
    }

    //wrapping in RowToInsert object to use them both for single and multiple insertion
    private InsertAllRequest.RowToInsert mapToRowAll(Map<String, Object> map) {
        return InsertAllRequest.RowToInsert.of(map);
    }

    //removing fields which are not required
    private InsertAllRequest.RowToInsert mapToRowReq(Map<String, Object> map) {
        map.remove("phone");
        map.remove("address");
        return InsertAllRequest.RowToInsert.of(map);
    }
}
