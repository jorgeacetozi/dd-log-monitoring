package com.jorgeacetozi.dd.logmonitoring.stats;

import java.util.Optional;
import java.util.stream.Collectors;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public class AverageTotalRequestsPerSecondStats extends Stats<Double> {

  public AverageTotalRequestsPerSecondStats(Storage storage, int pastSeconds) {
    super(storage, pastSeconds);
  }

  @Override
  public Double calculate() {
    return storage.getDataPoints(pastSeconds).stream()
        .collect(Collectors.summingDouble(DataPoint::getNumberOfRequests)) / pastSeconds;
  }

  @Override
  public Optional<String> calculateAndReturnAsString() {
    Double result = calculate();
    if (result == 0) {
      return Optional.empty();
    }

    String newLine = System.getProperty("line.separator");
    return Optional.of(new StringBuilder()
        .append(String.format("                AVERAGE REQUESTS PER SECOND: %.2f", result))
        .append(newLine).append("                ----------------------------").append(newLine)
        .toString());
  }
}
