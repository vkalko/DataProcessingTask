package org.example.vkalko.dataprocessing.service.impl;

import com.google.cloud.storage.Blob;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.example.vkalko.dataprocessing.WriteToDBTask;
import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AvroServiceImpl implements AvroService {

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
     * @param object object name in Google Storage used to retrieve data
     * @throws IOException
     */
    @Override
    public Iterator<Client> deserialize(Blob object) throws IOException {
        SeekableByteArrayInput byteArray = new SeekableByteArrayInput(object.getContent(Blob.BlobSourceOption.generationMatch()));
        DatumReader<Client> clientDatumReader = new SpecificDatumReader<>(Client.class);
        DataFileReader<Client> dataFileReader = new DataFileReader<>(byteArray, clientDatumReader);

        return dataFileReader.iterator();
    }

}
