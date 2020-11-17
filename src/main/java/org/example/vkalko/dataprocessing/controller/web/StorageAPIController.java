package org.example.vkalko.dataprocessing.controller.web;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.example.vkalko.dataprocessing.PushToQueueTask;
import org.example.vkalko.dataprocessing.WriteToDBTask;
import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.example.vkalko.dataprocessing.service.impl.AvroServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/gs")
public class StorageAPIController {

    private static final Logger logger = Logger.getLogger(StorageAPIController.class.getName());
    private static final String bucketName = "vkalko-dataprocess";
    private static final String projectId = "ultra-automata-294610";
    private static final String datasetName = "data_processing_task";
    private static final String tableNameRequired = "clients_required";
    private static final String tableNameComplete = "clients_complete";

    @PostMapping("/created")
    public ResponseEntity<String> fileCreated(
            @RequestBody Map<String, Object> body, @RequestHeader Map<String, String> headers) {

        // checking if POST request contains required field
        if (headers.get("ce-subject") == null) {
            String msg = "Missing expected header: ce-subject.";
            logger.log(Level.SEVERE, msg);
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }

        //getting filename added to GS
        String ceSubject = headers.get("ce-subject");
        String fileName = ceSubject.split("objects/")[1];
        String fileExtension = fileName.substring(fileName.lastIndexOf('.'));

        logger.info("Added file to GCS bucket: " + ceSubject);
        if (!fileExtension.equals(".avro")) {
            logger.info("Added unsupported file: " + fileName);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        }

        //initializing storage and getting Blob object
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.get(fileName);

        try {
            //initializing queues, executor, getting TableIDs and submitting tasks
            AvroService avroSerializer = new AvroServiceImpl();
            Iterator<Client> clientIterator = avroSerializer.deserialize(blob);

            BlockingQueue<List<InsertAllRequest.RowToInsert>> queue1 = new LinkedBlockingQueue<>(100);
            BlockingQueue<List<InsertAllRequest.RowToInsert>> queue2 = new LinkedBlockingQueue<>(100);

            BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
            TableId tableRequired = TableId.of(projectId, datasetName, tableNameRequired);
            TableId tableComplete = TableId.of(projectId, datasetName, tableNameComplete);

            PushToQueueTask pushToQueue = new PushToQueueTask(clientIterator, queue1, queue2);
            WriteToDBTask writeToDB1 = new WriteToDBTask(bigQuery, tableRequired, queue1, pushToQueue);
            WriteToDBTask writeToDB2 = new WriteToDBTask(bigQuery, tableComplete, queue2, pushToQueue);

            ExecutorService executorService = Executors.newFixedThreadPool(3);
            executorService.submit(pushToQueue);
            executorService.submit(writeToDB1);
            executorService.submit(writeToDB2);
            executorService.shutdown();
        } catch (IOException e) {
            String message = String.format("Error during deserialization of '%s': %s", fileName, e.getMessage());
            logger.log(Level.SEVERE, message);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/")
    public ResponseEntity<String> root() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
