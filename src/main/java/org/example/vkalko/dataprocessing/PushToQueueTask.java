package org.example.vkalko.dataprocessing;

import com.google.cloud.bigquery.InsertAllRequest;
import org.apache.avro.file.DataFileReader;
import org.example.vkalko.dataprocessing.model.Client;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class gets deserialized data from corresponding Iterator and puts the data into 2 queues,
 * making possible concurrently process it.
 */
public class PushToQueueTask implements Runnable {

    private Iterator<Client> clientIterator;
    private BlockingQueue<List<InsertAllRequest.RowToInsert>> requiredData;
    private BlockingQueue<List<InsertAllRequest.RowToInsert>> completeData;
    private volatile boolean finished;

    private static final Logger logger = Logger.getLogger(PushToQueueTask.class.getName());

    public PushToQueueTask(Iterator<Client> clientIterator, BlockingQueue<List<InsertAllRequest.RowToInsert>> requiredData,
                           BlockingQueue<List<InsertAllRequest.RowToInsert>> completeData) {
        this.clientIterator = clientIterator;
        this.requiredData = requiredData;
        this.completeData = completeData;
    }

    @Override
    public void run() {

        Map<String, Object> rowClientReq = new HashMap<>();
        Map<String, Object> rowAddress = new HashMap<>();
        Map<String, Object> rowClientComp = new HashMap<>();
        List<InsertAllRequest.RowToInsert> rowsToInsert1 = new ArrayList<>(50);
        List<InsertAllRequest.RowToInsert> rowsToInsert2 = new ArrayList<>(50);

        Client client;
        while (clientIterator.hasNext()) {
            client = clientIterator.next();

            //inserting required fields
            rowClientReq.put("id", client.getId());
            rowClientReq.put("name", client.getName().toString());
            rowClientReq.put("month_payment", client.getMonthPayment());

            //inserting record field address
            rowAddress.put("street", client.getAddress().getStreet().toString());
            rowAddress.put("city", client.getAddress().getCity().toString());
            rowAddress.put("zip", client.getAddress().getZip().toString());

            //inserting all fields
            rowClientComp.put("id", client.getId());
            rowClientComp.put("name", client.getName().toString());
            rowClientComp.put("phone", client.getPhone().toString());
            rowClientComp.put("address", rowAddress);
            rowClientComp.put("month_payment", client.getMonthPayment());

            //partitioning data into arrays of 50 elements to probably improve performance
            if (rowsToInsert1.size() > 0 && rowsToInsert1.size() % 50 == 0) {
                try {
                    requiredData.put(rowsToInsert1);
                    completeData.put(rowsToInsert2);
                    rowsToInsert1.clear();
                    rowsToInsert2.clear();
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Interrupted while waiting to put data into queue: " + e.getMessage());
                }
            } else {
                rowsToInsert1.add(InsertAllRequest.RowToInsert.of(rowClientReq));
                rowsToInsert2.add(InsertAllRequest.RowToInsert.of(rowClientComp));
            }

            client = null;
            rowClientReq.clear();
            rowAddress.clear();
            rowClientComp.clear();
        }
        try {
            if (rowsToInsert1.size() > 0) {
                requiredData.put(rowsToInsert1);
            }
            if (rowsToInsert2.size() > 0) {
                requiredData.put(rowsToInsert2);
            }
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Interrupted while waiting to put data into queue: " + ex.getMessage());
        }
        finished = true;
        logger.info("All data pushed to queues");
    }

    public boolean isFinished() {
        return finished;
    }

}
