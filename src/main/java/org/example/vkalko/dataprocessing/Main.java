package org.example.vkalko.dataprocessing;

import com.google.cloud.bigquery.*;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.example.vkalko.dataprocessing.model.Address;
import org.example.vkalko.dataprocessing.model.Client;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {


    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {

    }

    public static void testInsertion() {
        Map<String, Object> client = new HashMap<>();
        Map<String, Object> address = new HashMap<>();
        address.put("street", "Street");
        address.put("city", "City");
        address.put("zip", "Zip");
        client.put("id", 474747L);
        client.put("name", "Name");
        client.put("phone", "56565656");
        client.put("address", address);
        client.put("month_payment", 353.67);
        String projectName = "ultra-automata-294610";
        String dbName = "data_processing_task";
        BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
        TableId tableId = TableId.of(projectName, dbName, "clients_complete");
        InsertAllResponse response = bigQuery.insertAll(InsertAllRequest.newBuilder(tableId).addRow(client).build());
        if (response.hasErrors()) {
            for (Map.Entry<Long, List<BigQueryError>> entry: response.getInsertErrors().entrySet()) {
                logger.log(Level.SEVERE, entry.getValue().toString());
            }
        }
    }

    public static Iterator<FieldValueList> fetchRows(String query) throws InterruptedException {
        BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
        QueryJobConfiguration queryJob = QueryJobConfiguration.newBuilder(query).build();
        TableResult result = bigQuery.query(queryJob);
        return result.iterateAll().iterator();

    }

    public static void serialize() throws IOException {
        String projectName = "ultra-automata-294610";
        String dbName = "data_processing_task";
        String query = String.format("SELECT * FROM `%s.%s.%s`", projectName, dbName, "input_data");
        DatumWriter<Client> clientDatumWriter = new SpecificDatumWriter<>(Client.class);
        DataFileWriter<Client> dataFileWriter = new DataFileWriter<>(clientDatumWriter);
        dataFileWriter.create(Client.getClassSchema(), new File("clients.avro"));

        try {
            Iterator<FieldValueList> results = fetchRows(query);
            Client client;
            Address address;
            while (results.hasNext()) {
                FieldValueList item = results.next();
                address = new Address(item.get("address").getStringValue(),
                        item.get("city").getStringValue(),
                        item.get("zip").getStringValue());
                client = Client.newBuilder().
                        setId(item.get("id").getLongValue()).
                        setName(item.get("name").getStringValue()).
                        setPhone(item.get("phone").getStringValue()).
                        setAddress(address).
                        setMonthPayment(item.get("month_income").getDoubleValue()).
                        build();
                dataFileWriter.append(client);
//                System.out.printf("ID: %s; Name: %s; Phone: %s; Address: %s, %s, %s; Income: %f%n",
//                        item.get("id").getStringValue(), item.get("name").getStringValue(), item.get("phone").getStringValue(),
//                        item.get("address").getStringValue(), item.get("city").getStringValue(), item.get("zip").getStringValue(),
//                        item.get("month_income").getDoubleValue());
            }
            dataFileWriter.close();
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    public static void deserialize() throws IOException {
        File file = new File("clients.avro");
        DatumReader<Client> clientDatumReader = new SpecificDatumReader<>(Client.class);
        DataFileReader<Client> dataFileReader = new DataFileReader<>(file, clientDatumReader);
        Client client = null;
        while (dataFileReader.hasNext()) {
            client = dataFileReader.next();
            System.out.println(client);
        }
    }


}


