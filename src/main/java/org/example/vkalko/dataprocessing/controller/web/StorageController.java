package org.example.vkalko.dataprocessing.controller.web;

import org.example.vkalko.dataprocessing.service.AvroService;
import org.example.vkalko.dataprocessing.service.impl.AvroServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/gs")
public class StorageController {

    private static final String bucketName = "vkalko-dataprocess";

    private static final Logger logger = Logger.getLogger(StorageController.class.getName());

    @PostMapping("/created")
    public ResponseEntity<String> fileCreated(
            @RequestBody Map<String, Object> body, @RequestHeader Map<String, String> headers) {

        if (headers.get("ce-subject") == null) {
            String msg = "Missing expected header: ce-subject.";
            logger.log(Level.SEVERE, msg);
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }

        String ceSubject = headers.get("ce-subject");
        String fileName = ceSubject.split("objects/")[1];
        String fileExtension = fileName.substring(fileName.lastIndexOf('.'));

        logger.info("Added file to GCS bucket: " + ceSubject);
        if (!fileExtension.equals(".avro")) {
            logger.info("Added unsupported file: " + fileName);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        try {
            AvroService avroSerializer = new AvroServiceImpl();
            avroSerializer.deserialize(bucketName, fileName);
        } catch (IOException e) {
            String message = String.format("Error during deserialization of '%s': %s", fileName, e.getMessage());
            logger.log(Level.SEVERE, message);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/")
    public ResponseEntity<String> root() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
