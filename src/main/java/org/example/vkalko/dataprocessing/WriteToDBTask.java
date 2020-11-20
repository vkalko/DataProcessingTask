package org.example.vkalko.dataprocessing;

import org.example.vkalko.dataprocessing.dao.ClientDAO;
import org.example.vkalko.dataprocessing.model.Client;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * This class represents concurrent task, inserting data from queue into BigQuery.
 * PushToQueueTask object included to check, if thread producing data is finished, to avoid loss of data.
 */
public class WriteToDBTask implements Runnable {

    private final String tableName;
    private final BlockingQueue<List<Client>> queue;
    private final PushToQueueTask pushToQueue;

    private ClientDAO clientDAO;

    private static final Logger logger = Logger.getLogger(WriteToDBTask.class.getName());

    public WriteToDBTask(String tableName,
                         BlockingQueue<List<Client>> queue, PushToQueueTask pushToQueue) {
        this.tableName = tableName;
        this.queue = queue;
        this.pushToQueue = pushToQueue;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            while (!pushToQueue.isFinished() || !queue.isEmpty()) {
                while (queue.isEmpty()) {
                    Thread.onSpinWait();
                }
                clientDAO.insertAll(queue.poll(), tableName);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        logger.info("Task finished in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }

    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }
}
