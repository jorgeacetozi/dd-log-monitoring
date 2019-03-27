package com.jorgeacetozi.dd.logmonitoring.tasks;

import java.util.List;
import com.jorgeacetozi.dd.logmonitoring.alert.Alert;

/*
 * This task iterates over a list of alerts and execute each of them, which means two things:
 * 1) If the alert threshold is crossed, a notification will be sent out using the alert's handlers
 * 2) If the alert was active and its value is now below the threshold, a recovery notification is sent
 * out using the alert's handlers
 */
public class ExecuteAlertsTask implements Runnable {

  private final List<Alert> alerts;

  public ExecuteAlertsTask(List<Alert> alerts) {
    this.alerts = alerts;
  }

  @Override
  public void run() {
    for (Alert alert : alerts) {
      alert.execute();
    }
  }

}
