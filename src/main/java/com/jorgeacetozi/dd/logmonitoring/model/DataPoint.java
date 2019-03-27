package com.jorgeacetozi.dd.logmonitoring.model;

import java.util.ArrayList;
import java.util.List;

public class DataPoint {

  private List<Request> requests;

  public DataPoint() {
    this.requests = new ArrayList<>();
  }

  public DataPoint(Request request) {
    this();
    add(request);
  }

  public DataPoint(List<Request> requests) {
    this();
    add(requests);
  }

  public List<Request> getRequests() {
    return requests;
  }

  public boolean isEmpty() {
    return requests.size() == 0 ? true : false;
  }

  @Override
  public String toString() {
    return requests.toString();
  }

  public void add(List<Request> requests) {
    this.requests.addAll(requests);
  }

  public void add(Request request) {
    this.requests.add(request);
  }

  public int getNumberOfRequests() {
    return this.requests.size();
  }
}
