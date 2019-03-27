package com.jorgeacetozi.dd.logmonitoring.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import java.time.LocalDateTime;
import org.junit.Test;

public class RequestTest {

  @Test
  public void shouldCreateRequestFromLogLine() {
    String line =
        "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    Request request = new Request(line);

    assertThat(request.getAuthUser(), equalTo("jill"));
    assertThat(request.getBytes(), equalTo(234));
    assertThat(request.getDateTime(), equalTo(LocalDateTime.of(2018, 5, 9, 16, 0, 41)));
    assertThat(request.getHttpMethod(), equalTo("GET"));
    assertThat(request.getHttpVersion(), equalTo("HTTP/1.0"));
    assertThat(request.getRemoteHost(), equalTo("127.0.0.1"));
    assertThat(request.getRemoteLognameUser(), equalTo("-"));
    assertThat(request.getSection(), equalTo("/api"));
    assertThat(request.getStatus(), equalTo("200"));
    assertThat(request.getUri(), equalTo("/api/user"));
  }

  @Test
  public void shouldGetSectionFromURI() {
    String line = "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234";
    Request request = new Request(line);
    assertThat(request.getSection(), equalTo("/api"));

    line = "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api HTTP/1.0\" 200 234";
    request = new Request(line);
    assertThat(request.getSection(), equalTo("/api"));

    line = "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api/user/create/test HTTP/1.0\" 200 234";
    request = new Request(line);
    assertThat(request.getSection(), equalTo("/api"));
  }

}
