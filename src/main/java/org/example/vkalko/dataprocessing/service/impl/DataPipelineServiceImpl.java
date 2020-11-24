package org.example.vkalko.dataprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.vkalko.dataprocessing.PushToQueueTask;
import org.example.vkalko.dataprocessing.WriteToDBTask;
import org.example.vkalko.dataprocessing.dao.ClientDAO;
import org.example.vkalko.dataprocessing.model.Client;
import org.example.vkalko.dataprocessing.service.AvroService;
import org.example.vkalko.dataprocessing.service.DataPipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DataPipelineServiceImpl implements DataPipelineService {

    private final AvroService avroService;
    private final ClientDAO clientDAO;

    @Value("${spring.cloud.gcp.bigquery.table-name-req}")
    private String tableRequired;

    @Value("${spring.cloud.gcp.bigquery.table-name-comp}")
    private String tableComplete;

    @Override
    public void proceed(String bucketName, String objectName) throws IOException {
        //initializing queues, executor and submitting tasks
        Iterator<Client> clientIterator = avroService.deserialize(bucketName, objectName);

        BlockingQueue<List<Client>> qRequired = new ArrayBlockingQueue<>(50);
        BlockingQueue<List<Client>> qComplete = new ArrayBlockingQueue<>(50);

        PushToQueueTask pushToQueue = new PushToQueueTask(clientIterator, qRequired, qComplete);
        WriteToDBTask writeToDBReq = new WriteToDBTask(tableRequired, qRequired, pushToQueue);
        WriteToDBTask writeToDBComp = new WriteToDBTask(tableComplete, qComplete, pushToQueue);
        writeToDBReq.setClientDAO(clientDAO);
        writeToDBComp.setClientDAO(clientDAO);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(pushToQueue);
        executorService.submit(writeToDBReq);
        executorService.submit(writeToDBComp);
        executorService.shutdown();
    }
}
