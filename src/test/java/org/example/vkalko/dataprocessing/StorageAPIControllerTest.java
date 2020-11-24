package org.example.vkalko.dataprocessing;

import org.example.vkalko.dataprocessing.controller.web.ParsingException;
import org.example.vkalko.dataprocessing.controller.web.StorageAPIController;
import org.example.vkalko.dataprocessing.controller.web.UnexpectedEventTypeException;
import org.example.vkalko.dataprocessing.controller.web.UnsupportedFileTypeException;
import org.example.vkalko.dataprocessing.dto.PubSubNotificationDTO;
import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.example.vkalko.dataprocessing.service.DataPipelineService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StorageAPIControllerTest {

    @MockBean
    private AvroService avroService;

    @MockBean
    private DataPipelineService dataPipelineService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private StorageAPIController storageAPIController;

    @Test
    public void whenRequestValidReturnOk() throws Exception {
        String notification = "{\"message\": {\"attributes\": {\"eventType\": \"OBJECT_FINALIZE\", \"objectId\": \"clients.avro\"}}}";

        when(avroService.deserialize(new File("clients.avro")))
                .thenReturn(Arrays.stream(new Client[]{new Client()}).iterator());

        this.mvc.perform(post("/gs/created").contentType(MediaType.APPLICATION_JSON).content(notification))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test(expected = UnexpectedEventTypeException.class)
    public void shouldHandleInvalidEventType() {
        PubSubNotificationDTO pubSubNotificationDTO = new PubSubNotificationDTO();
        PubSubNotificationDTO.Message message = pubSubNotificationDTO.new Message();
        PubSubNotificationDTO.Message.Attributes attributes = message.new Attributes();
        attributes.setEventType("OBJECT_CREATE");
        attributes.setObjectId("file.avro");
        message.setAttributes(attributes);
        pubSubNotificationDTO.setMessage(message);
        storageAPIController.fileCreated(pubSubNotificationDTO);
    }

    @Test(expected = UnsupportedFileTypeException.class)
    public void shouldHandleInvalidFileType() {
        PubSubNotificationDTO pubSubNotificationDTO = new PubSubNotificationDTO();
        PubSubNotificationDTO.Message message = pubSubNotificationDTO.new Message();
        PubSubNotificationDTO.Message.Attributes attributes = message.new Attributes();
        attributes.setEventType("OBJECT_FINALIZE");
        attributes.setObjectId("file.txt");
        message.setAttributes(attributes);
        pubSubNotificationDTO.setMessage(message);
        storageAPIController.fileCreated(pubSubNotificationDTO);
    }

    @Test(expected = ParsingException.class)
    public void shouldHandleParsingException() throws IOException {
        Mockito.doThrow(new IOException()).when(dataPipelineService).proceed(any(), any());

        PubSubNotificationDTO pubSubNotificationDTO = new PubSubNotificationDTO();
        PubSubNotificationDTO.Message message = pubSubNotificationDTO.new Message();
        PubSubNotificationDTO.Message.Attributes attributes = message.new Attributes();
        attributes.setEventType("OBJECT_FINALIZE");
        attributes.setObjectId("file.avro");
        message.setAttributes(attributes);
        pubSubNotificationDTO.setMessage(message);
        storageAPIController.fileCreated(pubSubNotificationDTO);
    }
}
