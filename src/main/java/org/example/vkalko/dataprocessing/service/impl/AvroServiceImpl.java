package org.example.vkalko.dataprocessing.service.impl;

import com.google.cloud.storage.Blob;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.example.vkalko.dataprocessing.dao.StorageObjectDAO;
import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@Service
public class AvroServiceImpl implements AvroService {

    private final StorageObjectDAO storageObjectDAO;

    /**
     *
     * @param clients array of data to serialize
     * @param filename filepath, where serialized data will be stored
     * @throws IOException upon serialization error
     */
    @Override
    public void serialize(@NonNull Client[] clients, @NonNull String filename) throws IOException {
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
     * @param bucketName name of the bucket file added to
     * @param objectName object name in Google Storage used to retrieve data
     * @throws IOException upon deserialization error
     */
    @Override
    public Iterator<Client> deserialize(@NonNull String bucketName, @NonNull String objectName) throws IOException {
        Blob object = storageObjectDAO.getObject(bucketName, objectName);
        SeekableByteArrayInput byteArray =
                new SeekableByteArrayInput(object.getContent(Blob.BlobSourceOption.generationMatch()));
        DatumReader<Client> clientDatumReader = new SpecificDatumReader<>(Client.class);
        DataFileReader<Client> dataFileReader = new DataFileReader<>(byteArray, clientDatumReader);

        return dataFileReader.iterator();
    }

    /**
     *
     * @param file filename on local storage to deserialize
     * @throws IOException upon deserialization error
     */
    @Override
    public Iterator<Client> deserialize(@NonNull File file) throws IOException {
        DatumReader<Client> clientDatumReader = new SpecificDatumReader<>(Client.class);
        DataFileReader<Client> dataFileReader = new DataFileReader<>(file, clientDatumReader);

        return dataFileReader.iterator();
    }

}
