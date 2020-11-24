package org.example.vkalko.dataprocessing;

import org.example.vkalko.dataprocessing.controller.web.StorageAPIController;
import org.example.vkalko.dataprocessing.dto.PubSubNotificationDTO;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.example.vkalko.dataprocessing.service.DataPipelineService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataPipelineServiceImplTest {

    @SpyBean
    private DataPipelineService dataPipelineService;

    @MockBean
    private AvroService avroService;

    @Autowired
    private StorageAPIController storageAPIController;

    @Test
    public void shouldCallProceed() throws IOException {
        PubSubNotificationDTO pubSubNotificationDTO = new PubSubNotificationDTO();
        PubSubNotificationDTO.Message message = pubSubNotificationDTO.new Message();
        PubSubNotificationDTO.Message.Attributes attributes = message.new Attributes();
        attributes.setEventType("OBJECT_FINALIZE");
        attributes.setObjectId("file.avro");
        message.setAttributes(attributes);
        pubSubNotificationDTO.setMessage(message);

        storageAPIController.fileCreated(pubSubNotificationDTO);
        when(avroService.deserialize(any(), any())).thenReturn(null);
        verify(dataPipelineService).proceed(any(), any());
    }
}
