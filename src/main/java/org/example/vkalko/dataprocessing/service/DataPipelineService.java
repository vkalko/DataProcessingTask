package org.example.vkalko.dataprocessing.service;

import java.io.IOException;

public interface DataPipelineService {

    void proceed(String bucketName, String objectName) throws IOException;

}
