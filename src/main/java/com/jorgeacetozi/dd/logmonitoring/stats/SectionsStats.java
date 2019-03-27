package com.jorgeacetozi.dd.logmonitoring.stats;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.jorgeacetozi.dd.logmonitoring.model.Request;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public class SectionsStats extends Stats<Map<String, Long>> {

  public SectionsStats(Storage storage, int pastSeconds) {
    super(storage, pastSeconds);
  }

  @Override
  public Map<String, Long> calculate() {
    return storage.getDataPoints(pastSeconds).stream()
        .flatMap(dataPoint -> dataPoint.getRequests().stream())
        .collect(Collectors.groupingBy(Request::getSection, Collectors.counting()))
        .entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(5)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  @Override
  public Optional<String> calculateAndReturnAsString() {
    Map<String, Long> ranking = calculate();
    if (ranking.isEmpty()) {
      return Optional.empty();
    }

    String newLine = System.getProperty("line.separator");
    return Optional.of(new StringBuilder()
      .append("                  TOP 5 SECTIONS ACCESSED")
      .append(newLine)
      .append("                  -----------------------")
      .append(newLine)
      .append(ranking.entrySet()
        .stream()
        .map(entry -> entry.getKey() + " - " + entry.getValue())
        .collect(Collectors.joining(newLine)))
      .append(newLine)
      .toString());
  }
}
