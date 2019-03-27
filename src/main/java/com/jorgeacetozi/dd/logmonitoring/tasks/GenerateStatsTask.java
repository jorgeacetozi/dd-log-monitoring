package com.jorgeacetozi.dd.logmonitoring.tasks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import com.jorgeacetozi.dd.logmonitoring.DDLogMonitoringApp;
import com.jorgeacetozi.dd.logmonitoring.handler.Handler;
import com.jorgeacetozi.dd.logmonitoring.stats.Stats;

/*
 * This task iterates over a list of stats and calculates each of their values. If a result is
 * returned, it uses the alert's handlers to send a notification (Console, Slack, email, whatever)
 */
public class GenerateStatsTask implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(GenerateStatsTask.class.getName());
  private final List<Stats> statsList;
  private final List<Handler> handlers;

  public GenerateStatsTask(List<Stats> statsList, List<Handler> handlers) {
    this.statsList = statsList;
    this.handlers = handlers;
  }

  @Override
  public void run() {
    boolean firstStats = true;
    for (Stats stats : statsList) {
      for (Handler handler : handlers) {
        Optional<String> optionalResult = stats.calculateAndReturnAsString();
        if (optionalResult.isPresent()) {
          if (firstStats) {
            LOGGER.info("");
            LOGGER.info(String.format(
                "[%s] GENERATING STATS FOR THE PAST %d SECONDS\n"
                    + "------------------------------------------------------------------",
                LocalDateTime.now(), DDLogMonitoringApp.DISPLAY_STATS_INTERVAL_SEC));
            firstStats = false;
          }
          handler.sendNotification(optionalResult.get());
        }
      }
    }
    if (!firstStats) {
      LOGGER.info("------------------------------------------------------------------");
      LOGGER.info("");
      LOGGER.info("");
    }
  }

}
