package org.example.vkalko.dataprocessing;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.vkalko.dataprocessing.dao.ClientDAO;
import org.example.vkalko.dataprocessing.model.Client;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * This class represents concurrent task, inserting data from queue into BigQuery.
 * PushToQueueTask object included to check, if thread producing data is finished, to avoid loss of data.
 */
@Slf4j
@RequiredArgsConstructor
public class WriteToDBTask implements Runnable {

    private final String tableName;
    private final BlockingQueue<List<Client>> queue;
    private final PushToQueueTask pushToQueue;

    @Setter
    private ClientDAO clientDAO;

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
        log.info("Task finished in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }
}
