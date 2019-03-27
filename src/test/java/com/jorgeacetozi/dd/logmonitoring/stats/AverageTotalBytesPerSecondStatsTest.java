package com.jorgeacetozi.dd.logmonitoring.stats;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import java.util.Arrays;
import org.junit.Test;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;
import com.jorgeacetozi.dd.logmonitoring.model.Request;
import com.jorgeacetozi.dd.logmonitoring.storage.InMemoryStorage;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public class AverageTotalBytesPerSecondStatsTest {

  @Test
  public void shouldCalculateTheAverageBytesPerSecondForThePastSeconds() {
    Storage storage = new InMemoryStorage(5);
    Stats<Double> stats = new AverageTotalBytesPerSecondStats(storage, 5);
    assertThat(stats.calculate(), equalTo(0.0));

    String line = "127.0.0.1 - a [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(46.8));

    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(93.6));

    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(140.4));

    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(187.2));

    storage.insert(new DataPoint(new Request(line)));
    assertThat(stats.calculate(), equalTo(234.0));

    // Now we will duplicate the number of requests in the storage by adding
    // a new DataPoint with 6 requests (remember that the oldest data point will be evicted)
    storage.insert(new DataPoint(Arrays.asList(new Request(line), new Request(line),
        new Request(line), new Request(line), new Request(line), new Request(line))));
    assertThat(stats.calculate(), equalTo(468.0));
  }

}
