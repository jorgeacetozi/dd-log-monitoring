package com.jorgeacetozi.dd.logmonitoring.stats;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import java.util.Arrays;
import org.junit.Test;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;
import com.jorgeacetozi.dd.logmonitoring.model.Request;
import com.jorgeacetozi.dd.logmonitoring.storage.InMemoryStorage;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public class AverageTotalRequestsPerSecondStatsTest {

  @Test
  public void shouldCalculateTheAverageRequestsPerSecondForThePastSeconds() {
    Storage storage = new InMemoryStorage(5);
    Stats<Double> stats = new AverageTotalRequestsPerSecondStats(storage, 5);
    assertThat(stats.calculate(), equalTo(0.0));

    String line = "127.0.0.1 - a [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(0.2));

    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(0.4));

    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(0.6));

    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(0.8));

    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(1.0));

    // Inserting a DataPoint removes the oldest one, so the average keeps the same as this DataPoint
    // contains one request too
    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(1.0));

    // Same here
    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(1.0));

    // Now we have 5 data points, being 4 with only 1 request and the one below with 5
    // So the average for the past 5 seconds should be 2.0
    storage.insert(new DataPoint(Arrays.asList(new Request(line), new Request(line),
        new Request(line), new Request(line), new Request(line), new Request(line))));
    assertThat(stats.calculate(), equalTo(2.0));
  }

}
