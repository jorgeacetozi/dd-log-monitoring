package com.jorgeacetozi.dd.logmonitoring.alert;

import java.time.LocalDateTime;
import java.util.List;
import com.jorgeacetozi.dd.logmonitoring.handler.Handler;
import com.jorgeacetozi.dd.logmonitoring.stats.AverageTotalRequestsPerSecondStats;
import com.jorgeacetozi.dd.logmonitoring.stats.Stats;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public class AverageTotalRequestsPerSecondAlert extends Alert {

  private Stats<Double> avgTotalRequestsPerSecondsStats;
  private Double cachedResult;

  public AverageTotalRequestsPerSecondAlert(Storage storage, int threshold,
      List<Handler> handlers) {
    super(storage, threshold, handlers);
    avgTotalRequestsPerSecondsStats = new AverageTotalRequestsPerSecondStats(storage, storage.getCapacity());
  }

  @Override
  boolean crossedThreshold() {
    cachedResult = avgTotalRequestsPerSecondsStats.calculate();
    return cachedResult > threshold ? true : false;
  }

  @Override
  String getTriggerMessage() {
    return String.format("High traffic generated an alert - hits = %.2f req/sec, triggered at %s",
        cachedResult, LocalDateTime.now());
  }

  @Override
  String getRecoverMessage() {
    return String.format("High traffic alert recovered at %s", LocalDateTime.now());
  }

  Double getResult() {
    return this.cachedResult;
  }

}
