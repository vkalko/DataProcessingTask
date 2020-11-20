package org.example.vkalko.dataprocessing;

import org.example.vkalko.dataprocessing.model.Address;
import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AvroServiceImplTest {

    @Autowired
    private AvroService avroService;

    @Test
    public void deserializeFirstLocal() throws IOException {
        Address address = new Address("110 S 5th St", "Adair", "50002");
        Client client = Client.newBuilder()
                .setId(84197)
                .setName("Casey's General Store #2521 / Adair")
                .setPhone("237038692")
                .setAddress(address)
                .setMonthPayment(206.88)
                .build();

        Iterator<Client> clientIterator = avroService.deserialize(new File("clients.avro"));
        Assert.assertEquals(client, clientIterator.next());

    }

}
