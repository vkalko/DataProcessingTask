package org.example.vkalko.dataprocessing.controller.web;

import com.google.cloud.storage.Blob;
import org.example.vkalko.dataprocessing.dao.ClientDAO;
import org.example.vkalko.dataprocessing.dao.StorageObjectDAO;
import org.example.vkalko.dataprocessing.dto.PubSubNotificationDTO;
import org.example.vkalko.dataprocessing.PushToQueueTask;
import org.example.vkalko.dataprocessing.WriteToDBTask;
import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private final StorageObjectDAO storageObjectDAO;
    private final AvroService avroService;
    private final ClientDAO clientDAO;

    @Value("${spring.cloud.gcp.storage.bucket-name}")
    private String bucketName;

    @Value("${spring.cloud.gcp.bigquery.table-name-req}")
    private String tableRequired;

    @Value("${spring.cloud.gcp.bigquery.table-name-comp}")
    private String tableComplete;

    @Autowired
    public StorageAPIController(StorageObjectDAO storageObjectDAO, AvroService avroService,
                                ClientDAO clientDAO) {
        this.storageObjectDAO = storageObjectDAO;
        this.avroService = avroService;
        this.clientDAO = clientDAO;
    }

    //handles notifications from Google API about file creation in GS
    @PostMapping("/created")
    public ResponseEntity<String> fileCreated(
            @RequestBody PubSubNotificationDTO pubSubNotification,
            @RequestHeader Map<String, String> headers) {

        // checking if POST request contains required field
        PubSubNotificationDTO.Message message = pubSubNotification.getMessage();
        if (!message.getAttributes().getEventType().equals("OBJECT_FINALIZE")) {
            String badRequestMsg = "Unexpected attribute eventType. Expecting 'OBJECT_FINALIZE'.";
            return new ResponseEntity<>(badRequestMsg, HttpStatus.BAD_REQUEST);
        }

        //getting name of file added to GS
        String fileName = message.getAttributes().getObjectId();
        logger.info("Added file to GCS bucket: " + fileName);
        if (!fileName.endsWith(".avro")) {
            logger.info("Added unsupported file: " + fileName);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        try {
            //initializing queues, executor and submitting tasks
            Blob blob = storageObjectDAO.getObject(bucketName, fileName);
            Iterator<Client> clientIterator = avroService.deserialize(blob);

            BlockingQueue<List<Client>> qRequired = new ArrayBlockingQueue<>(50);
            BlockingQueue<List<Client>> qComplete = new ArrayBlockingQueue<>(50);

            PushToQueueTask pushToQueue = new PushToQueueTask(clientIterator, qRequired, qComplete);
            WriteToDBTask writeToDB1 = new WriteToDBTask(tableRequired, qRequired, pushToQueue);
            WriteToDBTask writeToDB2 = new WriteToDBTask(tableComplete, qComplete, pushToQueue);
            writeToDB1.setClientDAO(clientDAO);
            writeToDB2.setClientDAO(clientDAO);

            ExecutorService executorService = Executors.newFixedThreadPool(3);
            executorService.submit(pushToQueue);
            executorService.submit(writeToDB1);
            executorService.submit(writeToDB2);
            executorService.shutdown();
        } catch (IOException e) {
            String msg = String.format("Error during deserialization of '%s': %s", fileName, e.getMessage());
            logger.log(Level.SEVERE, msg);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/")
    public ResponseEntity<String> root() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
