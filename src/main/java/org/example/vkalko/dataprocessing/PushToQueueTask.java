package org.example.vkalko.dataprocessing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.vkalko.dataprocessing.model.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * This class gets deserialized data from corresponding Iterator and puts the data into 2 queues,
 * making possible concurrently process it.
 */
@Slf4j
@RequiredArgsConstructor
public class PushToQueueTask implements Runnable {

    private final Iterator<Client> clientIterator;
    private final BlockingQueue<List<Client>> requiredData;
    private final BlockingQueue<List<Client>> completeData;
    @Getter
    private volatile boolean finished;

    //number of elements pushed at a time
    private static final int ARRAY_SIZE = 50;

    @Override
    public void run() {

        List<Client> syncClients1 = Collections.synchronizedList(new ArrayList<>(ARRAY_SIZE));
        List<Client> syncClients2 = Collections.synchronizedList(new ArrayList<>(ARRAY_SIZE));
        Client client;

        while (clientIterator.hasNext()) {
            client = clientIterator.next();

            //partitioning data into arrays of 50 (default) elements to improve performance
            try {
                if (syncClients1.size() > 0 && syncClients1.size() % ARRAY_SIZE == 0) {
                    requiredData.put(syncClients1);
                    syncClients1 = Collections.synchronizedList(new ArrayList<>(ARRAY_SIZE));
                }
                syncClients1.add(client);
                if (syncClients2.size() > 0 && syncClients2.size() % ARRAY_SIZE == 0) {
                    completeData.put(syncClients2);
                    syncClients2 = Collections.synchronizedList(new ArrayList<>(ARRAY_SIZE));
                }
                syncClients2.add(client);

            } catch (InterruptedException e) {
                log.error("Interrupted while waiting to put data into queue: " + e.getMessage());
            }

        }
        try {
            if (syncClients1.size() > 0) {
                requiredData.put(syncClients1);
                completeData.put(syncClients2);
            }
        } catch (InterruptedException ex) {
            log.error("Interrupted while waiting to put data into queue: " + ex.getMessage());
        }
        finished = true;
        log.info("All data pushed to queues.");
    }
}
