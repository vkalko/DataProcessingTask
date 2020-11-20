package org.example.vkalko.dataprocessing.dao;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;

public interface StorageObjectDAO {

    Bucket getBucket(String name);
    Blob getObject(String bucketName, String objectName);

}
