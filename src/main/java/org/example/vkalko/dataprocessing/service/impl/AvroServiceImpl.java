package org.example.vkalko.dataprocessing.service.impl;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.example.vkalko.dataprocessing.WriteToDBTask;
import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class AvroServiceImpl implements AvroService {

    private static final String projectId = "ultra-automata-294610";
    private static final String datasetName = "data_processing_task";
    private static final String tableNameRequired = "clients_required";
    private static final String tableNameComplete = "clients_complete";

    /**
     *
     * @param clients array of data to serialize
     * @param filename filepath, where serialized data will be stored
     * @throws IOException
     */
    @Override
    public void serialize(Client[] clients, String filename) throws IOException {
        if (clients.length == 0) return;
        DatumWriter<Client> clientDatumWriter = new SpecificDatumWriter<>(Client.class);
        DataFileWriter<Client> dataFileWriter = new DataFileWriter<>(clientDatumWriter);
        Schema schema = clients[0].getSchema();
        dataFileWriter.create(schema, new File(filename));
        for (Client client: clients) {
            dataFileWriter.append(client);
        }
        dataFileWriter.close();
    }

    /**
     *
     * @param bucketName bucket name in Google Storage, where filename is
     * @param fileName object name in Google Storage used to retrieve data
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void deserialize(String bucketName, String fileName) throws IOException, InterruptedException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.get(fileName);

        SeekableByteArrayInput byteArray = new SeekableByteArrayInput(blob.getContent(Blob.BlobSourceOption.generationMatch()));
        DatumReader<Client> clientDatumReader = new SpecificDatumReader<>(Client.class);
        DataFileReader<Client> dataFileReader = new DataFileReader<>(byteArray, clientDatumReader);

        BlockingQueue<Map<String, Object>> queue1 = new LinkedBlockingQueue<>(100);
        BlockingQueue<Map<String, Object>> queue2 = new LinkedBlockingQueue<>(100);

        BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
        TableId tableRequired = TableId.of(projectId, datasetName, tableNameRequired);
        TableId tableComplete = TableId.of(projectId, datasetName, tableNameComplete);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        WriteToDBTask writeToDB1 = new WriteToDBTask(bigQuery, tableRequired, queue1);
        WriteToDBTask writeToDB2 = new WriteToDBTask(bigQuery, tableComplete, queue2);
        executorService.submit(writeToDB1);
        executorService.submit(writeToDB2);

        Map<String, Object> rowClientReq = new HashMap<>();
        Map<String, Object> rowAddress = new HashMap<>();
        Map<String, Object> rowClientComp = new HashMap<>();
        Client client;
        while (dataFileReader.hasNext()) {
            client = dataFileReader.next();
            rowClientReq.put("id", client.getId());
            rowClientReq.put("name", client.getName().toString());
            rowClientReq.put("month_payment", client.getMonthPayment());
            queue1.put(rowClientReq);

            rowAddress.put("street", client.getAddress().getStreet().toString());
            rowAddress.put("city", client.getAddress().getCity().toString());
            rowAddress.put("zip", client.getAddress().getZip().toString());
            rowClientComp.put("id", client.getId());
            rowClientComp.put("name", client.getName().toString());
            rowClientComp.put("phone", client.getPhone().toString());
            rowClientComp.put("address", rowAddress);
            rowClientComp.put("month_payment", client.getMonthPayment());
            queue2.put(rowClientComp);
        }
        writeToDB1.setFinished(true);
        writeToDB2.setFinished(true);
        executorService.awaitTermination(24, TimeUnit.HOURS);
    }

}
