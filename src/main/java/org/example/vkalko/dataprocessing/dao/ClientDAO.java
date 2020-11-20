package org.example.vkalko.dataprocessing.dao;

import com.google.cloud.bigquery.InsertAllResponse;
import org.example.vkalko.dataprocessing.model.Client;

public interface ClientDAO {

    InsertAllResponse insert(Client client, String tableName);

    InsertAllResponse insertAll(Iterable<Client> clients, String tableName);

}
