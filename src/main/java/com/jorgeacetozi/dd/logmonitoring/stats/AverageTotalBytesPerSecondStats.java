package com.jorgeacetozi.dd.logmonitoring.stats;

import java.util.Optional;
import java.util.stream.Collectors;
import com.jorgeacetozi.dd.logmonitoring.model.Request;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public class AverageTotalBytesPerSecondStats extends Stats<Double> {

  public AverageTotalBytesPerSecondStats(Storage storage, int pastSeconds) {
    super(storage, pastSeconds);
  }

  @Override
  public Double calculate() {
    return storage.getDataPoints(pastSeconds).stream()
        .flatMap(dataPoint -> dataPoint.getRequests().stream())
        .collect(Collectors.summingDouble(Request::getBytes)) / pastSeconds;
  }

  @Override
  public Optional<String> calculateAndReturnAsString() {
    Double result = calculate();
    if (result == 0) {
      return Optional.empty();
    }

    String newLine = System.getProperty("line.separator");
    return Optional.of(new StringBuilder()
      .append(String.format("                 AVERAGE BYTES PER SECOND: %.2f", result))
      .append(newLine)
      .append("                 -------------------------")
      .append(newLine)
      .toString());
  }

}
