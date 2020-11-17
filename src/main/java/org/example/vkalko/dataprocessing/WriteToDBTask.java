package org.example.vkalko.dataprocessing;

import com.google.cloud.bigquery.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents concurrent task, inserting data from queue into BigQuery.
 * PushToQueueTask object included to check, if thread producing data is finished, to avoid loss of data.
 */
public class WriteToDBTask implements Runnable {

    private final BigQuery bigQuery;
    private final TableId tableId;
    private final BlockingQueue<List<InsertAllRequest.RowToInsert>> queue;
    private final PushToQueueTask pushToQueue;

    private static final Logger logger = Logger.getLogger(WriteToDBTask.class.getName());

    public WriteToDBTask(BigQuery bigQuery, TableId tableId,
                         BlockingQueue<List<InsertAllRequest.RowToInsert>> queue, PushToQueueTask pushToQueue) {
        this.bigQuery = bigQuery;
        this.tableId = tableId;
        this.queue = queue;
        this.pushToQueue = pushToQueue;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (!pushToQueue.isFinished() || !queue.isEmpty()) {
            while (queue.isEmpty()) {
                Thread.onSpinWait();
            }
            insertRow(queue.poll());
        }
        logger.info("Task finished in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }

    public void insertRow(List<InsertAllRequest.RowToInsert> rows) {
        if (rows == null) return;
        InsertAllResponse response = bigQuery.insertAll(InsertAllRequest.newBuilder(tableId).
                setRows(rows).
                build());
        if (response.hasErrors()) {
            for (Map.Entry<Long, List<BigQueryError>> entry: response.getInsertErrors().entrySet()) {
                logger.log(Level.SEVERE, entry.getValue().toString());
            }
        }
    }
}
