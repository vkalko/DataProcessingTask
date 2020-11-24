package org.example.vkalko.dataprocessing.controller.web;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.vkalko.dataprocessing.dto.PubSubNotificationDTO;
import org.example.vkalko.dataprocessing.service.DataPipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__({@Autowired}))
@RestController
@RequestMapping("/gs")
public class StorageAPIController {

    private DataPipelineService dataPipelineService;

    //handles notifications from Google API about file creation in GS
    @PostMapping("/created")
    public ResponseEntity<String> fileCreated(
            @RequestBody PubSubNotificationDTO pubSubNotification) {

        // checking if POST request contains required field
        PubSubNotificationDTO.Message message = pubSubNotification.getMessage();
        checkEventType(message.getAttributes().getEventType());

        //getting name of file added to GS
        String fileName = message.getAttributes().getObjectId();
        String bucketName = message.getAttributes().getBucketId();

        //if added unsupported file, throw an exception
        checkFileType(fileName);

        log.info("Added file to GCS bucket: " + fileName);
        try {
            dataPipelineService.proceed(bucketName, fileName);
        } catch (IOException exception) {
            String errorMsg = String.format("Error parsing '%s': %s", fileName, exception.getMessage());
            throw new ParsingException(errorMsg);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void checkEventType(String eventType) {
        if (!eventType.equals("OBJECT_FINALIZE")) {
            String errorMsg = String.format("Unexpected attribute eventType '%s'. Expecting 'OBJECT_FINALIZE'.",
                    eventType);
            throw new UnexpectedEventTypeException(errorMsg);
        }
    }

    private void checkFileType(String fileName) {
        if (!fileName.endsWith(".avro")) {
            String fileType = fileName.indexOf('.') != -1? fileName.substring(fileName.lastIndexOf('.')): "";
            String errorMsg = String.format("Unsupported filetype '%s'. Expecting '.avro'", fileType);
            throw new UnsupportedFileTypeException(errorMsg);
        }
    }

    @RequestMapping("/")
    public ResponseEntity<String> root() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
