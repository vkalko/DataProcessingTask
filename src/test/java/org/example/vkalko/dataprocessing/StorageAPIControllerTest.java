package org.example.vkalko.dataprocessing;

import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.example.vkalko.dataprocessing.service.impl.AvroServiceImpl;
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
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StorageAPIControllerTest {

    @MockBean
    private AvroService avroService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void whenRequestValidReturnOk() throws Exception {
        avroService = Mockito.mock(AvroServiceImpl.class);

        Mockito.when(avroService.deserialize(new File("clients.avro")))
                .thenReturn(Arrays.stream(new Client[]{new Client()}).iterator());

        String notification = "{\"message\": {\"attributes\": {\"eventType\": \"OBJECT_FINALIZE\", \"objectId\": \"clients.avro\"}}}";

        this.mvc.perform(post("/gs/created").contentType(MediaType.APPLICATION_JSON).content(notification))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

    }
}
