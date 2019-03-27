package com.jorgeacetozi.dd.logmonitoring.stats;

import java.util.Optional;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public abstract class Stats<T> {

  protected final Storage storage;
  protected final int pastSeconds;

  public Stats(Storage storage, int pastSeconds) {
    this.storage = storage;
    this.pastSeconds = pastSeconds;
  }

  public abstract T calculate();

  public abstract Optional<String> calculateAndReturnAsString();

}
