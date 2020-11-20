package org.example.vkalko.dataprocessing.dao.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.example.vkalko.dataprocessing.dao.StorageObjectDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StorageObjectDAOImpl implements StorageObjectDAO {

    private final Storage storage;

    @Value("spring.cloud.gcp.config.project-id")
    private String projectId;

    public StorageObjectDAOImpl() {
        storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    }

    @Override
    public Bucket getBucket(String name) {
        return storage.get(name);
    }

    @Override
    public Blob getObject(String bucketName, String objectName) {
        return storage.get(bucketName, objectName);
    }
}
