package com.jorgeacetozi.dd.logmonitoring.alert;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;
import com.jorgeacetozi.dd.logmonitoring.handler.ConsoleHandler;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;
import com.jorgeacetozi.dd.logmonitoring.model.Request;
import com.jorgeacetozi.dd.logmonitoring.storage.InMemoryStorage;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public class AverageTotalRequestsPerSecondAlertTest {

  private static final int THRESHOLD = 1;

  @Test
  public void shouldAlertWhenTheThresholdIsCrossed() {
    Storage storage = new InMemoryStorage(5);
    AverageTotalRequestsPerSecondAlert alert = new AverageTotalRequestsPerSecondAlert(storage,
        THRESHOLD, Arrays.asList(new ConsoleHandler()));

    String line = "127.0.0.1 - a [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    storage.insert(new DataPoint(new Request(line)));
    storage.insert(new DataPoint(new Request(line)));
    storage.insert(new DataPoint(new Request(line)));
    storage.insert(new DataPoint(new Request(line)));
    storage.insert(new DataPoint(new Request(line)));

    assertThat(alert.execute(), equalTo(AlertStatus.OK));
    assertTrue(alert.getResult() <= THRESHOLD);

    storage.insert(new DataPoint(Arrays.asList(new Request(line), new Request(line))));
    assertThat(alert.execute(), equalTo(AlertStatus.ALERTING));
    assertTrue(alert.getResult() > THRESHOLD);
    assertThat(alert.getResult(), equalTo(1.2));
    assertTrue(alert.getTriggerMessage()
        .contains("High traffic generated an alert - hits = 1,20 req/sec"));
  }

  @Test
  public void shouldRecoverFromHighTraffic() {
    Storage storage = new InMemoryStorage(5);
    AverageTotalRequestsPerSecondAlert alert = new AverageTotalRequestsPerSecondAlert(storage,
        THRESHOLD, Arrays.asList(new ConsoleHandler()));

    String line = "127.0.0.1 - a [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    storage.insert(new DataPoint(new Request(line)));
    storage.insert(new DataPoint(new Request(line)));
    storage.insert(new DataPoint(new Request(line)));
    storage.insert(new DataPoint(new Request(line)));
    storage.insert(new DataPoint(Arrays.asList(new Request(line), new Request(line))));
    assertThat(alert.execute(), equalTo(AlertStatus.ALERTING));

    storage.insertEmptyDataPoints(1);
    assertThat(alert.execute(), equalTo(AlertStatus.OK));
    assertTrue(alert.getRecoverMessage().contains("High traffic alert recovered"));
  }

}
