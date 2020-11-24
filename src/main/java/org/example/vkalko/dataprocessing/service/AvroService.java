package org.example.vkalko.dataprocessing.service;

import com.google.cloud.storage.Blob;
import org.example.vkalko.dataprocessing.model.Client;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public interface AvroService {

    void serialize(Client[] records, String fileName) throws IOException;

    Iterator<Client> deserialize(String bucketName, String objectName) throws IOException;

    Iterator<Client> deserialize(File file) throws IOException;
}
