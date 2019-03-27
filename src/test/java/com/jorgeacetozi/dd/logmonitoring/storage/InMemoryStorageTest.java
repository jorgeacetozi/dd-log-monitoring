package com.jorgeacetozi.dd.logmonitoring.storage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;
import com.jorgeacetozi.dd.logmonitoring.model.Request;

public class InMemoryStorageTest {

  @Test
  public void shouldCreateStorageOfEmptyDataPoints() {
    Storage storage = new InMemoryStorage(3);
    assertTrue(storage.isFull());

    int numberOfRequests = storage.getDataPoints(3).stream()
        .collect(Collectors.summingInt(DataPoint::getNumberOfRequests));

    assertThat(numberOfRequests, equalTo(0));
  }

  @Test
  public void shouldDiscardDataPointsOlderThanTheStorageCapacity() {
    Storage storage = new InMemoryStorage(3);
    String line1 = "127.0.0.1 - a [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    Request request1 = new Request(line1);
    DataPoint dataPoint1 = new DataPoint(request1);

    String line2 = "127.0.0.1 - b [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    Request request2 = new Request(line2);
    DataPoint dataPoint2 = new DataPoint(request2);

    String line3 = "127.0.0.1 - c [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    Request request3 = new Request(line3);
    DataPoint dataPoint3 = new DataPoint(request3);

    String line4 = "127.0.0.1 - d [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    Request request4 = new Request(line4);
    DataPoint dataPoint4 = new DataPoint(request4);

    String line5 = "127.0.0.1 - e [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    Request request5 = new Request(line5);
    DataPoint dataPoint5 = new DataPoint(request5);

    storage.insert(dataPoint1);
    storage.insert(dataPoint2);
    storage.insert(dataPoint3);

    List<DataPoint> allDataPoints = storage.getDataPoints(3);
    assertThat(allDataPoints.get(0).getRequests().get(0).getAuthUser(), equalTo("a"));
    assertThat(allDataPoints.get(1).getRequests().get(0).getAuthUser(), equalTo("b"));
    assertThat(allDataPoints.get(2).getRequests().get(0).getAuthUser(), equalTo("c"));
    storage.insert(dataPoint4);
    storage.insert(dataPoint5);

    allDataPoints = storage.getDataPoints(3);
    assertThat(allDataPoints.get(0).getRequests().get(0).getAuthUser(), equalTo("c"));
    assertThat(allDataPoints.get(1).getRequests().get(0).getAuthUser(), equalTo("d"));
    assertThat(allDataPoints.get(2).getRequests().get(0).getAuthUser(), equalTo("e"));
  }

}
