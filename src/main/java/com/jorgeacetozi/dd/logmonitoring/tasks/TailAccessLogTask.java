package com.jorgeacetozi.dd.logmonitoring.tasks;

import static java.time.LocalDateTime.now;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.time.temporal.ChronoUnit;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;
import com.jorgeacetozi.dd.logmonitoring.model.Request;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;


/*
 * This task continuously reads new lines from the access.log and populate the in-memory Storage
 * with them. In order to find the correct position to insert into the storage, it subtracts the
 * request time from the current time (now() - request time) and takes converts the difference to
 * second unit.
 */
public class TailAccessLogTask implements Runnable {

  private final Storage storage;
  private final File accessLog;
  private final long scrapeInterval;
  private long currentPosition = 0;
  private boolean debug;

  public TailAccessLogTask(String accessLogPath, long interval, Storage storage, boolean debug) {
    accessLog = new File(accessLogPath);
    this.scrapeInterval = interval;
    this.storage = storage;
    this.debug = debug;
  }

  public void run() {
    try {
      while (true) {
        Thread.sleep(scrapeInterval);
        storage.insertEmptyDataPoints((int) scrapeInterval / 1000);

        if (accessLog.exists()) {
          if (accessLog.length() > currentPosition) {
            RandomAccessFile readWriteFileAccess = new RandomAccessFile(accessLog, "r");
            readWriteFileAccess.seek(currentPosition);
            String line = null;
            Request request;

            while ((line = readWriteFileAccess.readLine()) != null) {
              request = new Request(line);

              int index = currentTimeMinusRequestTimeInSeconds(request);
              if (isRequestWithinTheMonitoringPeriod(index)) {
                storage.insert(index, new DataPoint(request));
              }
            }
            currentPosition = readWriteFileAccess.getFilePointer();
            readWriteFileAccess.close();
          }
        } else {
          throw new FileNotFoundException("File " + accessLog.getAbsolutePath() + " not found");
        }
        if (debug) {
          storage.print();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private int currentTimeMinusRequestTimeInSeconds(Request request) {
    long deltaMillis = ChronoUnit.MILLIS.between(request.getDateTime(), now());
    return deltaMillis >= 0 ? (int) deltaMillis / 1000 : -1;
  }

  private boolean isRequestWithinTheMonitoringPeriod(int index) {
    return index >= 0 && index < storage.getCapacity();
  }
}
