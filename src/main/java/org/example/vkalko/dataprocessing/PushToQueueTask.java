package org.example.vkalko.dataprocessing;

import org.example.vkalko.dataprocessing.model.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class gets deserialized data from corresponding Iterator and puts the data into 2 queues,
 * making possible concurrently process it.
 */
public class PushToQueueTask implements Runnable {

    private final Iterator<Client> clientIterator;
    private final BlockingQueue<List<Client>> requiredData;
    private final BlockingQueue<List<Client>> completeData;
    private volatile boolean finished;

    private static final Logger logger = Logger.getLogger(PushToQueueTask.class.getName());

    public PushToQueueTask(Iterator<Client> clientIterator, BlockingQueue<List<Client>> requiredData,
                           BlockingQueue<List<Client>> completeData) {
        this.clientIterator = clientIterator;
        this.requiredData = requiredData;
        this.completeData = completeData;
    }

    @Override
    public void run() {

        List<Client> syncClients1 = Collections.synchronizedList(new ArrayList<>(50));
        List<Client> syncClients2 = Collections.synchronizedList(new ArrayList<>(50));
        Client client;

        while (clientIterator.hasNext()) {
            client = clientIterator.next();

            //partitioning data into arrays of 50 elements to probably improve performance
            try {
                if (syncClients1.size() > 0 && syncClients1.size() % 50 == 0) {
                    requiredData.put(syncClients1);
                    syncClients1 = Collections.synchronizedList(new ArrayList<>(50));
                }
                syncClients1.add(client);
                if (syncClients2.size() > 0 && syncClients2.size() % 50 == 0) {
                    completeData.put(syncClients2);
                    syncClients2 = Collections.synchronizedList(new ArrayList<>(50));
                }
                syncClients2.add(client);

            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Interrupted while waiting to put data into queue: " + e.getMessage());
            }

        }
        try {
            if (syncClients1.size() > 0) {
                requiredData.put(syncClients1);
                completeData.put(syncClients2);
            }
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Interrupted while waiting to put data into queue: " + ex.getMessage());
        }
        finished = true;
        logger.info("All data pushed to queues.");
    }

    public boolean isFinished() {
        return finished;
    }

}
