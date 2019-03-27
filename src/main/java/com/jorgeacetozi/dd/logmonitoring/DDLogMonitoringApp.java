package com.jorgeacetozi.dd.logmonitoring;

import static com.jorgeacetozi.dd.logmonitoring.util.PropertiesUtil.getOrDefault;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import com.jorgeacetozi.dd.logmonitoring.alert.Alert;
import com.jorgeacetozi.dd.logmonitoring.alert.AverageTotalRequestsPerSecondAlert;
import com.jorgeacetozi.dd.logmonitoring.handler.ConsoleHandler;
import com.jorgeacetozi.dd.logmonitoring.handler.Handler;
import com.jorgeacetozi.dd.logmonitoring.stats.AverageTotalBytesPerSecondStats;
import com.jorgeacetozi.dd.logmonitoring.stats.AverageTotalRequestsPerSecondStats;
import com.jorgeacetozi.dd.logmonitoring.stats.ResponseCodesStats;
import com.jorgeacetozi.dd.logmonitoring.stats.SectionsStats;
import com.jorgeacetozi.dd.logmonitoring.stats.Stats;
import com.jorgeacetozi.dd.logmonitoring.storage.InMemoryStorage;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;
import com.jorgeacetozi.dd.logmonitoring.tasks.GenerateStatsTask;
import com.jorgeacetozi.dd.logmonitoring.tasks.TailAccessLogTask;
import com.jorgeacetozi.dd.logmonitoring.tasks.ExecuteAlertsTask;

public class DDLogMonitoringApp {

  private static final int STORAGE_SIZE = 120;
  public static final int DISPLAY_STATS_INTERVAL_SEC = 10;
  private static final int EXECUTE_ALERTS_INTERVAL_SEC = 1;
  private static final String ACCESS_LOG_PATH = "/tmp/access.log";
  private static final int SCRAPE_INTERVAL_MS = 1000;
  private static final int ALERT_AVG_TOTAL_REQ_PER_SEC_THRESHOLD = 10;

  public static void main(String[] args) {
    // Get argument
    final boolean debug;
    if (args.length != 0 && args[0].equals("--debug")) {
      debug = true;
    } else {
      debug = false;
    }
    // Set log format
    System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s%n");

    // Get parameters from dd-log-monitoring.properties file
    // If the file doesn't exist, fall back to default values
    final String accessLogPath = getOrDefault("accesslog.path", ACCESS_LOG_PATH);
    final long scrapeIntervalMillis = getOrDefault("scrape.interval.ms", SCRAPE_INTERVAL_MS);
    final int alertAvgTotalReqPerSecThreshold = getOrDefault(
        "alert.avgTotalRequestsPerSecond.threshold", ALERT_AVG_TOTAL_REQ_PER_SEC_THRESHOLD);

    // Create thread pools
    final ExecutorService fileReaderExecutor = Executors.newFixedThreadPool(5);
    final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(5);

    // Create storage, Console Handler, alert, and statistics
    final Storage storage = new InMemoryStorage(STORAGE_SIZE);
    final List<Handler> handlers = Arrays.asList(new ConsoleHandler());
    final List<Alert> alerts = Arrays.asList(
        new AverageTotalRequestsPerSecondAlert(storage, alertAvgTotalReqPerSecThreshold, handlers));

    final List<Stats> statsList =
        Arrays.asList(new SectionsStats(storage, DISPLAY_STATS_INTERVAL_SEC),
            new ResponseCodesStats(storage, DISPLAY_STATS_INTERVAL_SEC),
            new AverageTotalRequestsPerSecondStats(storage, DISPLAY_STATS_INTERVAL_SEC),
            new AverageTotalBytesPerSecondStats(storage, DISPLAY_STATS_INTERVAL_SEC));

    // Submit tasks to the thread pools
    scheduledExecutor.scheduleAtFixedRate(new GenerateStatsTask(statsList, handlers), 5,
        DISPLAY_STATS_INTERVAL_SEC, SECONDS);

    scheduledExecutor.scheduleAtFixedRate(new ExecuteAlertsTask(alerts), 5,
        EXECUTE_ALERTS_INTERVAL_SEC, SECONDS);

    fileReaderExecutor
        .execute(new TailAccessLogTask(accessLogPath, scrapeIntervalMillis, storage, debug));

    // Shutdown the thread pools if the app is gracefully shutdown
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        scheduledExecutor.shutdown();
        fileReaderExecutor.shutdown();
      }
    });
  }
}
