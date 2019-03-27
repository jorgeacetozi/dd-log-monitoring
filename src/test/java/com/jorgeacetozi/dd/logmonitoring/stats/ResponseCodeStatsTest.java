package com.jorgeacetozi.dd.logmonitoring.stats;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.jorgeacetozi.dd.logmonitoring.model.DataPoint;
import com.jorgeacetozi.dd.logmonitoring.model.Request;
import com.jorgeacetozi.dd.logmonitoring.storage.InMemoryStorage;
import com.jorgeacetozi.dd.logmonitoring.storage.Storage;

public class ResponseCodeStatsTest {

  @Test
  public void shouldCalculateTheTopFiveResponseCodes() {
    Storage storage = new InMemoryStorage(5);
    Stats<Map<String, Long>> responseCodesStats = new ResponseCodesStats(storage, 5);

    String line1 =
        "127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123";
    Request request1 = new Request(line1);
    DataPoint dataPoint1 = new DataPoint(request1);

    String line2 =
        "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    Request request2 = new Request(line2);
    DataPoint dataPoint2 = new DataPoint(request2);

    String line3 =
        "127.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200 34";
    Request request3 = new Request(line3);
    DataPoint dataPoint3 = new DataPoint(request3);

    String line4 =
        "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 503 12";
    Request request4 = new Request(line4);
    DataPoint dataPoint4 = new DataPoint(request4);

    storage.insert(dataPoint1);
    storage.insert(dataPoint2);
    storage.insert(dataPoint3);
    storage.insert(dataPoint4);
    storage.insertEmptyDataPoints(1);

    Map<String, Long> topFiveSections = responseCodesStats.calculate();
    List<String> keys = new ArrayList<>(topFiveSections.keySet());

    assertThat(keys.get(0), equalTo("HTTP 200"));
    assertThat(keys.get(1), equalTo("HTTP 503"));
    assertThat(topFiveSections.get(keys.get(0)), equalTo(3l));
    assertThat(topFiveSections.get(keys.get(1)), equalTo(1l));
  }

}
