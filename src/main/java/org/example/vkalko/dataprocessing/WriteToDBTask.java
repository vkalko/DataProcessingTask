package org.example.vkalko.dataprocessing;

import com.google.cloud.bigquery.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteToDBTask implements Runnable {

    private final BigQuery bigQuery;
    private final TableId tableId;
    private final BlockingQueue<Map<String, Object>> queue;
    private volatile boolean finished;

    private static final Logger logger = Logger.getLogger(WriteToDBTask.class.getName());

    public WriteToDBTask(BigQuery bigQuery, TableId tableId, BlockingQueue<Map<String, Object>> queue) {
        this.bigQuery = bigQuery;
        this.tableId = tableId;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!finished || !queue.isEmpty()) {
            while (queue.isEmpty()) {
                Thread.onSpinWait();
            }
            insertRow(queue.poll());
        }
        logger.info("Task finished");
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void insertRow(Map<String, Object> row) {
        if (row == null) return;
        InsertAllResponse response = bigQuery.insertAll(InsertAllRequest.newBuilder(tableId).
                addRow(row).
                build());
        if (response.hasErrors()) {
            for (Map.Entry<Long, List<BigQueryError>> entry: response.getInsertErrors().entrySet()) {
                logger.log(Level.SEVERE, entry.getValue().toString());
            }
        }
    }
}
