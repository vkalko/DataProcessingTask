package org.example.vkalko.dataprocessing.service;

import org.example.vkalko.dataprocessing.model.Client;

import java.io.IOException;

public interface AvroService {

    void serialize(Client[] records, String fileName) throws IOException;

    void deserialize(String bucketName, String fileName) throws IOException, InterruptedException;
}
