package com.jorgeacetozi.dd.logmonitoring.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {

  private String remoteHost;
  private String remoteLognameUser;
  private String authUser;
  private LocalDateTime dateTime;
  private String httpMethod;
  private String uri;
  private String httpVersion;
  private String status;
  private int bytes;

  public Request(String line) {
    final String regex =
        "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+) (\\S+)\\s*(\\S+)?\\s*\" (\\d{3}) (\\S+)";
    final Pattern pattern = Pattern.compile(regex);
    final Matcher matcher = pattern.matcher(line);
    matcher.find();

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    this.remoteHost = matcher.group(1);
    this.remoteLognameUser = matcher.group(2);
    this.authUser = matcher.group(3);
    this.dateTime = LocalDateTime.parse(matcher.group(4), formatter);
    this.httpMethod = matcher.group(5);
    this.uri = matcher.group(6);
    this.httpVersion = matcher.group(7);
    this.status = matcher.group(8);
    this.bytes = Integer.parseInt(matcher.group(9));
  }

  public String getRemoteHost() {
    return remoteHost;
  }

  public String getRemoteLognameUser() {
    return remoteLognameUser;
  }

  public String getAuthUser() {
    return authUser;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public String getUri() {
    return uri;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public String getStatus() {
    return status;
  }

  public int getBytes() {
    return bytes;
  }

  public String getSection() {
    int indexOfLastSlash = uri.lastIndexOf("/");
    if (indexOfLastSlash == 0) {
      return this.uri;
    } else {
      return uri.substring(0, uri.indexOf("/", uri.indexOf("/") + 1));
    }
  }

  @Override
  public String toString() {
    return this.authUser;
  }
}
