package com.jorgeacetozi.dd.logmonitoring.alert;

import java.util.List;
import com.jorgeacetozi.dd.logmonitoring.handler.Handler;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public abstract class Alert {

  final Storage storage;
  private final List<Handler> handlers;
  private AlertStatus status;
  final int threshold;

  public Alert(Storage storage, int threshold, List<Handler> handlers) {
    this.storage = storage;
    this.handlers = handlers;
    this.threshold = threshold;
    status = AlertStatus.OK;
  }

  public AlertStatus execute() {
    if (crossedThreshold()) {
      status = AlertStatus.ALERTING;
      trigger();
    } else {
      if (status.equals(AlertStatus.ALERTING)) {
        recover();
        status = AlertStatus.OK;
      }
    }
    return status;
  }

  void trigger() {
    for (Handler handler : handlers) {
      handler.sendNotification(getTriggerMessage());
    }
  }

  void recover() {
    for (Handler handler : handlers) {
      handler.sendNotification(getRecoverMessage());
    }
  }

  abstract boolean crossedThreshold();

  abstract String getTriggerMessage();

  abstract String getRecoverMessage();

}
